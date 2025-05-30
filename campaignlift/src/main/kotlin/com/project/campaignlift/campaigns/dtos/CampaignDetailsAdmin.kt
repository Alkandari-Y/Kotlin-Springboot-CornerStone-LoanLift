package com.project.campaignlift.campaigns.dtos

import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.campaignlift.pledges.dtos.PledgeOverviewDto
import com.project.campaignlift.pledges.dtos.toPledgeOverviewDto
import java.math.BigDecimal
import java.time.LocalDate

data class CampaignDetailsAdmin (
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
    var amountRaised: BigDecimal,
    val files: List<FileDto>,
    val pledges: List<PledgeOverviewDto>
)


fun CampaignEntity.toCampaignDetailsAdmin(amountRaised: BigDecimal): CampaignDetailsAdmin {
    return CampaignDetailsAdmin(
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
        amountRaised = amountRaised,
        files = this.files.map { it.toDto() },
        pledges = this.pledges.map { it.toPledgeOverviewDto() }
    )
}