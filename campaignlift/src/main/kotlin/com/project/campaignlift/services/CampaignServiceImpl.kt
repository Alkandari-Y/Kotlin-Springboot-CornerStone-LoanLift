package com.project.campaignlift.services

import com.project.banking.entities.AccountEntity
import com.project.campaignlift.campaigns.dtos.*
import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.campaignlift.repositories.AccountRepository
import com.project.campaignlift.repositories.CampaignRepository
import com.project.campaignlift.repositories.CommentRepository
import com.project.campaignlift.repositories.PledgeRepository
import com.project.common.enums.AccountType
import com.project.common.exceptions.campaigns.CampaignDeletionNotAllowedException
import com.project.common.exceptions.campaigns.CampaignNotFoundException
import com.project.common.exceptions.campaigns.CampaignPermissionDeniedException
import com.project.common.exceptions.campaigns.CampaignUpdateNotAllowedException
import com.project.common.exceptions.kycs.IncompleteUserRegistrationException
import com.project.common.responses.authenthication.UserInfoDto
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal

@Service
class CampaignServiceImpl(
    private val campaignRepository: CampaignRepository,
    private val fileStorageService: FileStorageService,
    private val commentRepository: CommentRepository,
    private val accountRepository: AccountRepository,
    private val pledgeRepository: PledgeRepository,
) : CampaignService {
    override fun getAllCampaigns(): List<CampaignListItemResponse> {
        return campaignRepository.listAllCampaigns()
    }

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
                active = true,
                balance = BigDecimal.ZERO
            )
        )
        val imageUrl = fileStorageService.uploadFilePublic(image)

        val campaign = campaignRepository.save(
            campaignDto.toEntity(
                    createdBy = user.userId,
                    imageUrl = imageUrl,
                    accountId = campaignAccount.id!!
            )
        )
        return campaign
    }

    override fun updateCampaign(
        campaignId: Long,
        userId: Long,
        campaign: UpdateCampaignRequest,
        image: MultipartFile?
    ): CampaignEntity {
        val existing = campaignRepository.findByIdOrNull(campaignId)
            ?: throw CampaignNotFoundException()

        if (existing.createdBy != userId) {
            throw CampaignPermissionDeniedException(userId, campaignId)
        }

        if (existing.status != CampaignStatus.NEW) {
            throw CampaignUpdateNotAllowedException( existing.status.name)
        }

        val imageUrl = image?.let {
            fileStorageService.uploadFilePublic(it)
        } ?: existing.imageUrl.orEmpty()

        val updatedCampaign = campaign.toEntity(
            imageUrl = imageUrl,
            previousCampaign = existing
        ).copy(id = existing.id)

        return campaignRepository.save(updatedCampaign)
    }


    override fun deleteCampaign(campaignId: Long, authUserId: Long) {
        val campaign = campaignRepository.findByIdOrNull(campaignId)
            ?: throw CampaignNotFoundException()

        if (campaign.createdBy != authUserId) {
            throw CampaignPermissionDeniedException(authUserId, campaignId)
        }

        if (campaign.status != CampaignStatus.NEW) {
            throw CampaignDeletionNotAllowedException(campaignId, campaign.status.name)
        }

        campaignRepository.deleteById(campaignId)
    }

    override fun getCampaignDetails(campaignId: Long): CampaignWithCommentsDto? {
        val campaign = campaignRepository.findByIdOrNull(campaignId)
            ?: throw CampaignNotFoundException()

        val comments = commentRepository.findByCampaignId(campaignId)
        val amountRaised = pledgeRepository.getTotalCommittedAmountForCampaign(campaignId)
        return campaign.toCampaignWithCommentsDto(amountRaised, comments)
    }

    override fun getAllByUserId(userId: Long): List<CampaignListItemResponse> {
        return campaignRepository.findByCreatedId(userId)
    }

    override fun getAllCampaignsByStatus(status: CampaignStatus): List<CampaignListItemResponse> {
        return campaignRepository.findByStatus(status)
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
        return campaignRepository.save(updatedCampaign)
    }
}