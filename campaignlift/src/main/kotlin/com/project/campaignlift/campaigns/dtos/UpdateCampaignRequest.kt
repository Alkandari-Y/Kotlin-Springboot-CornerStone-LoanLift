package com.project.campaignlift.campaigns.dtos

import com.project.banking.entities.CategoryEntity
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

    @field:DateTimeFormat(pattern = "dd-MM-yyyy")
    @field:NotBlank
    val campaignDeadline: String,
)

fun UpdateCampaignRequest.toEntity(
    imageUrl: String,
    previousCampaign: CampaignEntity,
    interestRate: BigDecimal = BigDecimal("0.02").setScale(3),
    repaymentMonths: Int = 10 * 12,
    category: CategoryEntity
): CampaignEntity = CampaignEntity(
    id = previousCampaign.id,
    createdBy = previousCampaign.createdBy,
    title = this.title,
    description = this.description,
    goalAmount = this.goalAmount,
    interestRate = interestRate,
    repaymentMonths = repaymentMonths,
    category = category,
    campaignDeadline = LocalDate.parse(campaignDeadline, dateFormatter),
    imageUrl = imageUrl,
    status = previousCampaign.status,
    submittedAt = previousCampaign.submittedAt,
    approvedBy = previousCampaign.approvedBy,
    accountId = previousCampaign.accountId
)
