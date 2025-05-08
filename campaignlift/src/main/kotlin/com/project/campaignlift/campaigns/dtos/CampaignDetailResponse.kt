package com.project.campaignlift.campaigns.dtos

import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.campaignlift.entities.CommentEntity
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
    var comments: List<CommentResponseDto> = emptyList()
)


fun CampaignEntity.toDetailResponse(comments: List<CommentResponseDto> = emptyList()) = CampaignDetailResponse(
    createdBy = createdBy!!,
    categoryId = categoryId!!,
    title = title,
    description = description!!,
    goalAmount = goalAmount!!,
    interestRate = interestRate,
    repaymentMonths = repaymentMonths,
    status = status,
    submittedAt = submittedAt!!,
    approvedBy = approvedBy,
    campaignDeadline = campaignDeadline!!,
    accountId = accountId!!,
    imageUrl = imageUrl!!,
    amountRaised = amountRaised,
    comments = comments
)