package com.project.campaignlift.campaigns.dtos

import com.project.campaignlift.entities.CampaignEntity
import com.project.banking.entities.CategoryEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.common.utils.dateFormatter
import java.math.BigDecimal
import jakarta.validation.constraints.*
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class CreateCampaignDto(
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

fun CreateCampaignDto.toEntity(
    createdBy: Long,
    imageUrl: String,
    accountId: Long,
    approvedBy: Long? = null,
    interestRate: BigDecimal = BigDecimal("0.02").setScale(3),
    repaymentMonths: Int = 10 * 12,
    category: CategoryEntity
) = CampaignEntity(
    createdBy = createdBy,
    title = this.title,
    description = this.description,
    goalAmount = this.goalAmount,
    interestRate = interestRate,
    repaymentMonths = repaymentMonths,
    category = category,
    campaignDeadline = LocalDate.parse(this.campaignDeadline, dateFormatter),
    imageUrl = imageUrl,
    status = CampaignStatus.NEW,
    submittedAt = LocalDate.now(),
    approvedBy = approvedBy,
    accountId = accountId
)