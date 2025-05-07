package com.project.campaignlift.campaigns.dtos

import com.project.campaignlift.entities.CampaignStatus
import java.math.BigDecimal
import java.time.LocalDate

data class CampaignDto (
    val createdBy: Long,
    val categoryId: Long,
    val title: String,
    val description: String,
    val goalAmount: BigDecimal,
    val interestRate: BigDecimal,
    val repaymentMonths: Int,
    val status: CampaignStatus,
    val submittedAt: LocalDate,
    val approvedBy: Long?,
    val campaignDeadline: LocalDate,
    val accountId: Long?,
    val imageUrl: String,
    var amountRaised: BigDecimal? = null
)