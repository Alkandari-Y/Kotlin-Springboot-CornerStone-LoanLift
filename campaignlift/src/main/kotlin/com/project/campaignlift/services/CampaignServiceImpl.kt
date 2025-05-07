package com.project.campaignlift.services

import com.project.campaignlift.campaigns.dtos.CampaignDto
import com.project.campaignlift.campaigns.dtos.CreateCampaignDto
import com.project.campaignlift.campaigns.dtos.UpdateCampaignRequest
import com.project.campaignlift.campaigns.dtos.toEntity
import com.project.campaignlift.campaigns.utils.saveMultipartFileLocally
import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.repositories.CampaignRepositories
import com.project.common.exceptions.APIException
import com.project.common.exceptions.ErrorCode
import com.project.common.responses.authenthication.UserInfoDto
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class CampaignServiceImpl(
    private val campaignRepository: CampaignRepositories,
    private val fileStorageService: FileStorageService,
): CampaignService {
    override fun getAllCampaigns(): List<CampaignEntity> {
        return campaignRepository.findAll()
    }

    override fun getCampaignById(id: Long): CampaignEntity? {
        return campaignRepository.findByIdOrNull(id)
    }

    override fun createCampaign(
        campaignDto: CreateCampaignDto,
        user: UserInfoDto,
        image: MultipartFile
    ): CampaignEntity {
        if (user.isActive.not()) {
            throw APIException(
                message = "User must complete KYC registration",
                code = ErrorCode.INCOMPLETE_USER_REGISTRATION,
                httpStatus = HttpStatus.BAD_REQUEST
            )
        }
        val account = 1L
        val imageUrl = fileStorageService.uploadFile(image)
        val campaign = campaignDto.toEntity(
            createdBy = user.userId,
            imageUrl = imageUrl,
            accountId = account
        )
        return campaignRepository.save(campaign)
    }

    override fun updateCampaign(campaignId: Long, userId: Long, campaign: UpdateCampaignRequest): CampaignEntity {
        val campaignToUpdate = campaignRepository.findByIdOrNull(campaignId)

        val updatedCampaign = CampaignEntity()
        return updatedCampaign
    }

    override fun deleteCampaign(id: Long) {
        campaignRepository.deleteById(id)
    }
}