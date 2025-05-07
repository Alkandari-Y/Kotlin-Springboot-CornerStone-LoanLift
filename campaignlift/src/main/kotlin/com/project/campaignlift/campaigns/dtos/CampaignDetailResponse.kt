package com.project.campaignlift.campaigns.dtos

import com.project.campaignlift.entities.CampaignStatus
import java.math.BigDecimal
import java.time.Instant

data class CampaignDetailResponse(
    val createdBy: Long,
    val categoryId: Long,
    val title: String,
    val description: String,
    val goalAmount: BigDecimal,
    val interestRate: BigDecimal,
    val repaymentMonths: Int,
    val status: CampaignStatus,
    val submittedAt: Instant,
    val approvedBy: Long,
    val campaignDeadline: Instant,
    val accountId: Long,
    val imageUrl: String,
    var amountRaised: BigDecimal
)
