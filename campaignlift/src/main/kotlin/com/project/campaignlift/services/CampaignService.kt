package com.project.campaignlift.services

import com.project.campaignlift.campaigns.dtos.CampaignPublicDetails
import com.project.campaignlift.campaigns.dtos.CampaignPublicDetailsWithComments
import com.project.campaignlift.campaigns.dtos.CreateCampaignDto
import com.project.campaignlift.campaigns.dtos.UpdateCampaignRequest
import com.project.campaignlift.campaigns.dtos.CampaignListItemResponse
import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.common.responses.authenthication.UserInfoDto
import org.springframework.web.multipart.MultipartFile

interface CampaignService {

    fun createCampaign(
        campaignDto: CreateCampaignDto,
        user: UserInfoDto,
        image: MultipartFile,
    ): CampaignEntity

    fun updateCampaign(
        campaignId: Long,
        user: UserInfoDto,
        campaign: UpdateCampaignRequest,
        image: MultipartFile?
    ): CampaignEntity

    fun approveRejectCampaignStatus(
        campaignId: Long,
        status: CampaignStatus,
        adminId: Long? = null
    ): CampaignEntity

    fun getCampaignById(campaignId: Long): CampaignEntity?

    fun changeCampaignStatus(
        campaignId: Long,
        status: CampaignStatus
    ): CampaignEntity


    fun getAllCampaignsByStatus(status: CampaignStatus): List<CampaignListItemResponse>
    fun getAllApprovedCampaigns(): List<CampaignListItemResponse>
    fun getAllByUserId(userId: Long): List<CampaignListItemResponse>

    fun getPublicCampaignDetailsById(campaignId: Long): CampaignPublicDetails?
    fun getPublicCampaignDetailsWithCommentsById(campaignId: Long): CampaignPublicDetailsWithComments?

    fun deleteCampaign(campaignId: Long, user: UserInfoDto)
}

