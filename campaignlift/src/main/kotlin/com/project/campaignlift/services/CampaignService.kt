package com.project.campaignlift.services

import com.project.campaignlift.campaigns.dtos.CampaignDto
import com.project.campaignlift.campaigns.dtos.CreateCampaignDto
import com.project.campaignlift.campaigns.dtos.UpdateCampaignRequest
import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.common.responses.authenthication.UserInfoDto
import org.springframework.web.multipart.MultipartFile

interface CampaignService {
    fun getAllCampaigns(): List<CampaignEntity>
    fun getCampaignById(id: Long): CampaignEntity?
    fun createCampaign(
        campaignDto: CreateCampaignDto,
        user: UserInfoDto,
        image: MultipartFile,
        accountId: Long
    ): CampaignEntity
    fun updateCampaign(campaignId: Long, userId: Long, campaign: UpdateCampaignRequest): CampaignEntity
    fun deleteCampaign(id: Long)

    fun getALlByUserId(userId: Long): List<CampaignEntity>
    fun getAllCampaignsByStatus(status: CampaignStatus): List<CampaignEntity>
    fun changeCampaignStatus(campaignId: Long, status: CampaignStatus): CampaignEntity
    fun approveRejectCampaignStatus(campaignId: Long, status: CampaignStatus, adminId: Long? = null): CampaignEntity
}