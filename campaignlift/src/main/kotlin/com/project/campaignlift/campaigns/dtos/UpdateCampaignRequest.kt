package com.project.campaignlift.campaigns.dtos

import com.project.campaignlift.entities.CampaignEntity
import com.project.common.utils.dateFormatter
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.format.annotation.DateTimeFormat
import java.math.BigDecimal
import java.time.LocalDate


data class UpdateCampaignRequest(
    @field:NotNull
    val categoryId: Long,

    @field:NotBlank
    val title: String,

    @field:NotBlank
    val description: String,

    @field:DecimalMin("1000.000")
    val goalAmount: BigDecimal,

    @field:NotNull
    @field:DecimalMin("0.000")
    val interestRate: BigDecimal,

    @NotNull
    @field:Min(1)
    val accountId: Long,

    @field:Min(3)
    val repaymentMonths: Int,

    @field:DateTimeFormat(pattern = "dd-MM-yyyy")
    @field:NotBlank
    val campaignDeadline: String,
)

fun UpdateCampaignRequest.toEntity(
    imageUrl: String,
    previousCampaign: CampaignEntity
): CampaignEntity = CampaignEntity(
    id = previousCampaign.id,
    createdBy = previousCampaign.createdBy,
    title = this.title,
    description = this.description,
    goalAmount = this.goalAmount,
    interestRate = this.interestRate,
    repaymentMonths = this.repaymentMonths,
    categoryId = this.categoryId,
    campaignDeadline = LocalDate.parse(campaignDeadline, dateFormatter),
    imageUrl = imageUrl,
    status = previousCampaign.status,
    submittedAt = previousCampaign.submittedAt,
    approvedBy = previousCampaign.approvedBy,
    accountId = accountId
)
