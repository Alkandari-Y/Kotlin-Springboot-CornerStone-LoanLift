package com.project.campaignlift.services

import com.project.banking.entities.AccountEntity
import com.project.campaignlift.campaigns.dtos.*
import com.project.campaignlift.campaigns.dtos.CampaignListItemResponse
import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.campaignlift.repositories.*
import com.project.common.enums.AccountType
import com.project.common.exceptions.campaigns.CampaignDeletionNotAllowedException
import com.project.common.exceptions.campaigns.CampaignNotFoundException
import com.project.common.exceptions.campaigns.CampaignPermissionDeniedException
import com.project.common.exceptions.campaigns.CampaignUpdateNotAllowedException
import com.project.common.exceptions.categories.CategoryNotFoundException
import com.project.common.exceptions.kycs.IncompleteUserRegistrationException
import com.project.common.responses.authenthication.UserInfoDto
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal

@Service
class CampaignServiceImpl(
    private val campaignRepository: CampaignRepository,
    private val categoryRepository: CategoryRepository,
    private val fileStorageService: FileStorageService,
    private val commentRepository: CommentRepository,
    private val accountRepository: AccountRepository,
    private val pledgeRepository: PledgeRepository,
    private val mailService: MailService,
    @Value("\${aws.endpoint}")
     val endpoint: String
) : CampaignService {

    override fun getCampaignById(id: Long): CampaignDetailResponse? {
        val campaign = campaignRepository.findByIdOrNull(id)
            ?: throw CampaignNotFoundException()

        val amountRaised = pledgeRepository.getTotalCommittedAmountForCampaign(id)

        return campaign.toDetailResponse(
            amountRaised = amountRaised
        )
    }


    @Transactional
    override fun createCampaign(
        campaignDto: CreateCampaignDto,
        user: UserInfoDto,
        image: MultipartFile,
    ): CampaignEntity {
        if (user.isActive.not()) {
            throw IncompleteUserRegistrationException()
        }
        val campaignAccount = accountRepository.save(
    AccountEntity(
                name = "${campaignDto.title} Account",
                ownerId = user.userId,
                ownerType = AccountType.CAMPAIGN,
                active = false,
                balance = BigDecimal.ZERO
            )
        )
        val (publicBucket, imageUrl) = fileStorageService.uploadFile(image, true)
        val category = categoryRepository.findByIdOrNull(campaignDto.categoryId)
            ?: throw CategoryNotFoundException()

        val campaign = campaignRepository.save(
            campaignDto.toEntity(
                createdBy = user.userId,
                imageUrl = "$endpoint/$publicBucket/$imageUrl",
                accountId = campaignAccount.id!!,
                category = category
            )
        )
        mailService.sendHtmlEmail(
            to = user.email,
            subject = campaignDto.title,
            bodyText = "Your ${campaignDto.title} has been create successfully and is pending review",
            username = user.username,
        )
        return campaign
    }

    override fun updateCampaign(
        campaignId: Long,
        user: UserInfoDto,
        campaign: UpdateCampaignRequest,
        image: MultipartFile?
    ): CampaignEntity {
        val existing = campaignRepository.findByIdOrNull(campaignId)
            ?: throw CampaignNotFoundException()

        if (existing.createdBy != user.userId) {
            throw CampaignPermissionDeniedException(user.userId, campaignId)
        }

        if (existing.status != CampaignStatus.NEW) {
            throw CampaignUpdateNotAllowedException( existing.status.name)
        }

        val category = categoryRepository.findByIdOrNull(campaign.categoryId)
            ?: throw CategoryNotFoundException()

        val imageUrl = image?.let {
            val (_, url) = fileStorageService.uploadFile(it, true)
            url
        } ?: existing.imageUrl.orEmpty()

        val updatedCampaign = campaign.toEntity(
            imageUrl = imageUrl,
            previousCampaign = existing,
            category = category
        ).copy(id = existing.id)
        mailService.sendHtmlEmail(
            to = user.email,
            subject = "${campaign.title} Updated",
            bodyText = "Your ${campaign.title} has been updated successfully and is pending review",
            username = user.username,
        )
        return campaignRepository.save(updatedCampaign)
    }


    override fun deleteCampaign(campaignId: Long, user: UserInfoDto) {
        val campaign = campaignRepository.findByIdOrNull(campaignId)
            ?: throw CampaignNotFoundException()

        if (campaign.createdBy != user.userId) {
            throw CampaignPermissionDeniedException(user.userId, campaignId)
        }

        if (campaign.status != CampaignStatus.NEW) {
            throw CampaignDeletionNotAllowedException(campaignId, campaign.status.name)
        }

        campaignRepository.deleteById(campaignId)
        mailService.sendHtmlEmail(
            to = user.email,
            subject = "${campaign.title} Removed",
            bodyText = "Your ${campaign.title} has been deleted successfully",
            username = user.username,
        )
    }

    override fun getCampaignDetails(campaignId: Long): CampaignWithCommentsDto? {
        val campaign = campaignRepository.findByIdOrNull(campaignId)
            ?: throw CampaignNotFoundException()

        val comments = commentRepository.findByCampaignId(campaignId)
        val amountRaised = pledgeRepository.getTotalCommittedAmountForCampaign(campaignId)
        return campaign.toCampaignWithCommentsDto(amountRaised, comments)
    }

    override fun getCampaignEntityById(campaignId: Long): CampaignEntity? {
        return campaignRepository.findByIdOrNull(campaignId)
    }

    override fun getAllByUserId(userId: Long): List<CampaignListItemResponse> {
        return campaignRepository.listAllCampaignsByUserId(userId)
    }

    override fun getAllCampaignsByStatus(status: CampaignStatus): List<CampaignListItemResponse> {
        return campaignRepository.listAllCampaignsByStatus(status)
    }

    override fun getAllApprovedCampaigns(): List<CampaignListItemResponse> {
        return campaignRepository.listAllApprovedCampaigns()
    }

    override fun changeCampaignStatus(campaignId: Long, status: CampaignStatus): CampaignEntity {
        val campaign = campaignRepository.findByIdOrNull(campaignId)
            ?: throw CampaignNotFoundException()

        val updatedCampaign = campaign.copy(status = status)
        return campaignRepository.save(updatedCampaign)
    }

    override fun approveRejectCampaignStatus(campaignId: Long, status: CampaignStatus, adminId: Long?): CampaignEntity {
        val campaign = campaignRepository.findByIdOrNull(campaignId)
            ?: throw CampaignNotFoundException()

        val updatedCampaign = campaign.copy(status = status, approvedBy = adminId)
        val campaignUpdated = campaignRepository.save(updatedCampaign)

        return campaignUpdated
    }
}