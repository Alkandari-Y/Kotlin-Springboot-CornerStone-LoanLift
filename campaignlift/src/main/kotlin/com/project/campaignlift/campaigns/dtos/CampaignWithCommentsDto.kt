package com.project.campaignlift.campaigns.dtos

import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.campaignlift.entities.projections.CommentProjection

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
    val campaignDeadline: LocalDate?,
    val imageUrl: String?,
    val amountRaised: BigDecimal = BigDecimal.ZERO,
    val comments: List<CommentProjection> = emptyList()
)


fun CampaignEntity.toCampaignWithCommentsDto(
    amountRaised: BigDecimal = BigDecimal.ZERO,
    comments: List<CommentProjection> = emptyList()
): CampaignWithCommentsDto {
    return CampaignWithCommentsDto(
        id = this.id!!,
        title = this.title,
        description = this.description,
        goalAmount = this.goalAmount,
        interestRate = this.interestRate,
        repaymentMonths = this.repaymentMonths,
        status = this.status,
        campaignDeadline = this.campaignDeadline,
        createdBy = this.createdBy,
        categoryId = this.category?.id,
        imageUrl = this.imageUrl,
        amountRaised = amountRaised,
        comments = comments,
    )
}