package com.project.campaignlift.admin.dtos

import com.project.campaignlift.entities.CampaignStatus

import jakarta.validation.constraints.NotNull

data class CampaignStatusRequest(
    @field:NotNull
    val name: CampaignStatus
)
