package com.project.campaignlift.campaigns.dtos

import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import java.math.BigDecimal
import java.time.LocalDate

data class CampaignDetailResponse(
    val createdBy: Long,
    val categoryId: Long,
    val title: String,
    val description: String,
    val goalAmount: BigDecimal,
    val interestRate: BigDecimal,
    val repaymentMonths: Int,
    val status: CampaignStatus,
    val submittedAt: LocalDate,
    val approvedBy: Long? = null,
    val campaignDeadline: LocalDate,
    val accountId: Long,
    val imageUrl: String,
    var amountRaised: BigDecimal,
)


fun CampaignEntity.toDetailResponse(
    amountRaised: BigDecimal = BigDecimal.ZERO
) = CampaignDetailResponse(
    createdBy = this.createdBy!!,
    categoryId =this. categoryId!!,
    title = this.title,
    description = this.description!!,
    goalAmount = this.goalAmount!!,
    interestRate = this.interestRate,
    repaymentMonths = this.repaymentMonths,
    status = this.status,
    submittedAt = this.submittedAt!!,
    approvedBy = this.approvedBy,
    campaignDeadline = this.campaignDeadline!!,
    accountId = this.accountId!!,
    imageUrl = this.imageUrl!!,
    amountRaised = amountRaised,
)