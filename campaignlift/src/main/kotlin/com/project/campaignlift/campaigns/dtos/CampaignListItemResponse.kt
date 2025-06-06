package com.project.campaignlift.campaigns.dtos


import com.project.campaignlift.entities.CampaignStatus
import java.math.BigDecimal
import java.time.LocalDate

data class CampaignListItemResponse(
    val id: Long,
    val createdBy: Long,
    val categoryId: Long,
    val title: String,
    val goalAmount: BigDecimal,
    val status: CampaignStatus,
    val campaignDeadline: LocalDate?,
    val imageUrl: String?,
    val amountRaised: BigDecimal,
    val categoryName: String,
)
