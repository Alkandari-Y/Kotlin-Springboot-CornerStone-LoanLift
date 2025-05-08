package com.project.campaignlift.campaigns.dtos

import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.campaignlift.entities.CommentEntity

import java.math.BigDecimal
import java.time.LocalDate

data class CampaignWithCommentsDto(
    val id: Long?,
    val createdBy: Long?,
    val categoryId: Long?,
    val title: String,
    val description: String?,
    val goalAmount: BigDecimal?,
    val interestRate: BigDecimal,
    val repaymentMonths: Int,
    val status: CampaignStatus,
    val submittedAt: LocalDate?,
    val approvedBy: Long?,
    val campaignDeadline: LocalDate?,
    val accountId: Long?,
    val imageUrl: String?,
    val amountRaised: BigDecimal = BigDecimal.ZERO,
    val comments: List<CommentResponseDto> = emptyList()
)


fun CampaignEntity.toCampaignWithCommentsDto(comments: List<CommentResponseDto> = emptyList()): CampaignWithCommentsDto {
    return CampaignWithCommentsDto(
        id = id,
        createdBy = createdBy,
        categoryId = categoryId,
        title = title,
        description = description,
        goalAmount = goalAmount,
        interestRate = interestRate,
        repaymentMonths = repaymentMonths,
        status = status,
        submittedAt = submittedAt,
        approvedBy = approvedBy,
        campaignDeadline = campaignDeadline,
        accountId = accountId,
        imageUrl = imageUrl,
        amountRaised = amountRaised,
        comments = this.comments?.map { it.toResponseDto() } ?: comments
    )
}
//
//fun CommentEntity.toResponseDto(): CommentResponseDto {
//    return CommentResponseDto(
//        id = id!!,
//        campaignId = campaign?.id!!,
//        message = message!!,
//        createdBy = createdBy!!,
//        createdAt = createdAt!!
//    )
//}