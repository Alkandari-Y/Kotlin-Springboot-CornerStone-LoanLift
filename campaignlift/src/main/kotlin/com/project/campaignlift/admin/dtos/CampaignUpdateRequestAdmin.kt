package com.project.campaignlift.admin.dtos

import com.project.banking.entities.CategoryEntity
import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.common.exceptions.campaigns.InvalidCampaignStatusChangeException
import com.project.common.utils.dateFormatter
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.format.annotation.DateTimeFormat
import java.math.BigDecimal
import java.time.LocalDate

data class CampaignUpdateRequestAdmin(
    @field:NotBlank
    val title: String,

    @field:NotBlank
    val description: String,

    @field:Min(1)
    @field:NotNull
    val categoryId: Long,

    @field:DecimalMin("1000.000")
    val goalAmount: BigDecimal,

    @field:DecimalMin("0.02")
    val interestRate: BigDecimal,

    @field:Min(10)
    val repaymentMonths: Int,

    @field:DateTimeFormat(pattern = "dd-MM-yyyy")
    @field:NotBlank
    val campaignDeadline: String,

    @field:NotNull
    val status: CampaignStatus
)



fun CampaignUpdateRequestAdmin.toAdminUpdatedEntity(
    previousCampaign: CampaignEntity,
    category: CategoryEntity,
    adminUserId: Long?
) : CampaignEntity {
    val allowedStatuses = listOf(
        CampaignStatus.PENDING,
        CampaignStatus.REJECTED,
        CampaignStatus.ACTIVE
    )
    if (status !in allowedStatuses) {
        throw InvalidCampaignStatusChangeException(
            "Invalid status change for campaign. Allowed statuses: ${allowedStatuses.joinToString(", ")}"
        )
    }

    if (status !in allowedStatuses.filter{ it == CampaignStatus.PENDING } && adminUserId == null) {
        throw InvalidCampaignStatusChangeException("Admin user id is required for pending status change")
    }

    return  CampaignEntity(
        id = previousCampaign.id!!,
        createdBy = previousCampaign.createdBy!!,
        title = this.title,
        description = this.description,
        goalAmount = this.goalAmount,
        interestRate = this.interestRate,
        repaymentMonths = this.repaymentMonths,
        status = this.status,
        submittedAt = previousCampaign.submittedAt!!,
        approvedBy = previousCampaign.approvedBy,
        campaignDeadline = LocalDate.parse(this.campaignDeadline, dateFormatter),
        accountId = previousCampaign.accountId!!,
        imageUrl = previousCampaign.imageUrl,
        category = category,
    )
}