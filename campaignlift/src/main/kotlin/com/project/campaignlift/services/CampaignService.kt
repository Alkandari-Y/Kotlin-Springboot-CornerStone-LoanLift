package com.project.campaignlift.services

import com.project.campaignlift.campaigns.dtos.CampaignListItemResponse
import com.project.campaignlift.campaigns.dtos.CampaignWithCommentsDto
import com.project.campaignlift.campaigns.dtos.CreateCampaignDto
import com.project.campaignlift.campaigns.dtos.UpdateCampaignRequest
import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.common.responses.authenthication.UserInfoDto
import org.springframework.web.multipart.MultipartFile

interface CampaignService {
    fun getAllCampaigns(): List<CampaignListItemResponse>
    fun getAllCampaignsByStatus(status: CampaignStatus): List<CampaignListItemResponse>
    fun getAllByUserId(userId: Long): List<CampaignListItemResponse>

    fun getCampaignDetails(campaignId: Long): CampaignWithCommentsDto?

    fun getCampaignById(id: Long): CampaignEntity?
    fun updateCampaign(
        campaignId: Long,
        userId: Long,
        campaign: UpdateCampaignRequest,
        image: MultipartFile?
    ): CampaignEntity
    fun createCampaign(
        campaignDto: CreateCampaignDto,
        user: UserInfoDto,
        image: MultipartFile,
        accountId: Long
    ): CampaignEntity
    fun changeCampaignStatus(campaignId: Long, status: CampaignStatus): CampaignEntity
    fun approveRejectCampaignStatus(campaignId: Long, status: CampaignStatus, adminId: Long? = null): CampaignEntity
    fun deleteCampaign(campaignId: Long, authUserId: Long)
}