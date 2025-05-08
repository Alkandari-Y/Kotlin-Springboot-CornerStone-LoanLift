package com.project.campaignlift.services

import com.project.campaignlift.campaigns.dtos.*
import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.campaignlift.repositories.CampaignRepositories
import com.project.campaignlift.repositories.CommentRepository
import com.project.common.exceptions.APIException
import com.project.common.exceptions.ErrorCode
import com.project.common.responses.authenthication.UserInfoDto
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal

@Service
class CampaignServiceImpl(
    private val campaignRepository: CampaignRepositories,
    private val fileStorageService: FileStorageService,
    private val commentRepository: CommentRepository,
) : CampaignService {
    override fun getAllCampaigns(): List<CampaignListItemResponse> {
        return campaignRepository.listAllCampaigns()
    }

    override fun getCampaignById(id: Long): CampaignEntity? {
        return campaignRepository.findByIdOrNull(id)
    }

    override fun createCampaign(
        campaignDto: CreateCampaignDto,
        user: UserInfoDto,
        image: MultipartFile,
        accountId: Long
    ): CampaignEntity {
        if (user.isActive.not()) {
            throw APIException(
                message = "User must complete KYC registration",
                code = ErrorCode.INCOMPLETE_USER_REGISTRATION,
                httpStatus = HttpStatus.BAD_REQUEST
            )
        }

        val imageUrl = fileStorageService.uploadFile(image)
        val campaign = campaignDto.toEntity(
            createdBy = user.userId,
            imageUrl = imageUrl,
            accountId = accountId
        )
        return campaignRepository.save(campaign)
    }

    override fun updateCampaign(
        campaignId: Long,
        userId: Long,
        campaign: UpdateCampaignRequest
    ): CampaignEntity {
        campaignRepository.findByIdOrNull(campaignId)
        val updatedCampaign = CampaignEntity()
        return updatedCampaign
    }

    override fun deleteCampaign(id: Long) {
        campaignRepository.deleteById(id)
    }

    override fun getCampaignDetails(campaignId: Long): CampaignWithCommentsDto? {
        val campaign = campaignRepository.findByIdOrNull(campaignId)
            ?: throw APIException(
                "Campaign with id $campaignId not found",
                HttpStatus.NOT_FOUND,
                ErrorCode.ACCOUNT_NOT_FOUND
            )
        val comments = commentRepository.findByCampaignId(campaignId)
        val amountRaised = BigDecimal.ZERO
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
            ?: throw APIException(
                "Campaign with id $campaignId not found",
                HttpStatus.NOT_FOUND,
                ErrorCode.ACCOUNT_NOT_FOUND
            )

        val updatedCampaign = campaign.copy(status = status)
        return campaignRepository.save(updatedCampaign)
    }

    override fun approveRejectCampaignStatus(campaignId: Long, status: CampaignStatus, adminId: Long?): CampaignEntity {
        val campaign = campaignRepository.findByIdOrNull(campaignId)
            ?: throw APIException(
                "Campaign with id $campaignId not found",
                HttpStatus.NOT_FOUND,
                ErrorCode.ACCOUNT_NOT_FOUND
            )

        val updatedCampaign = campaign.copy(status = status, approvedBy = adminId)
        return campaignRepository.save(updatedCampaign)
    }
}