package com.project.campaignlift.campaigns.dtos

import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.campaignlift.entities.projections.CommentProjection
import java.math.BigDecimal
import java.time.LocalDate

sealed interface CampaignDetailResponse

data class CampaignPublicDetails(
    val id: Long,
    val createdBy: Long,
    val categoryId: Long,
    val categoryName: String,
    val title: String,
    val description: String,
    val goalAmount: BigDecimal,
    val interestRate: BigDecimal,
    val repaymentMonths: Int,
    val status: CampaignStatus,
    val submittedAt: LocalDate,
    val campaignDeadline: LocalDate,
    val imageUrl: String,
    var amountRaised: BigDecimal,
)  : CampaignDetailResponse

data class CampaignPublicDetailsWithComments(
    val id: Long?,
    val createdBy: Long?,
    val categoryId: Long?,
    val categoryName: String,
    val title: String,
    val description: String?,
    val goalAmount: BigDecimal?,
    val interestRate: BigDecimal,
    val repaymentMonths: Int,
    val status: CampaignStatus,
    val submittedAt: LocalDate,
    val campaignDeadline: LocalDate?,
    val imageUrl: String?,
    val amountRaised: BigDecimal = BigDecimal.ZERO,
    val comments: List<CommentProjection> = emptyList()
) : CampaignDetailResponse


fun CampaignEntity.toPublicDetailsWithComments(
    amountRaised: BigDecimal = BigDecimal.ZERO,
    comments: List<CommentProjection> = emptyList()
): CampaignPublicDetailsWithComments {
    return CampaignPublicDetailsWithComments(
        id = this.id!!,
        createdBy = this.createdBy!!,
        categoryId =this.category?.id!!,
        categoryName = this.category?.name!!,
        title = this.title,
        description = this.description,
        goalAmount = this.goalAmount,
        interestRate = this.interestRate,
        repaymentMonths = this.repaymentMonths,
        status = this.status,
        submittedAt = this.submittedAt!!,
        campaignDeadline = this.campaignDeadline,
        imageUrl = this.imageUrl,
        amountRaised = amountRaised,
        comments = comments,
    )
}

fun CampaignEntity.toPublicDetails(
    amountRaised: BigDecimal = BigDecimal.ZERO
) = CampaignPublicDetails(
    id = this.id!!,
    createdBy = this.createdBy!!,
    categoryId =this.category?.id!!,
    categoryName = this.category?.name!!,
    title = this.title,
    description = this.description!!,
    goalAmount = this.goalAmount!!,
    interestRate = this.interestRate,
    repaymentMonths = this.repaymentMonths,
    status = this.status,
    submittedAt = this.submittedAt!!,
    campaignDeadline = this.campaignDeadline!!,
    imageUrl = this.imageUrl!!,
    amountRaised = amountRaised,
)