package com.project.campaignlift.admin.dtos

import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import java.math.BigDecimal
import java.time.LocalDate

data class CampaignDetailsBasicAdmin(
    val id: Long,
    val createdBy: Long,
    val accountId: Long,
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
)


fun CampaignEntity.toCampaignDetailsBasicAdmin(): CampaignDetailsBasicAdmin {
    return CampaignDetailsBasicAdmin(
        id = this.id!!,
        createdBy = this.createdBy!!,
        accountId = this.accountId!!,
        categoryId = this.category?.id!!,
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
    )
}