package com.project.campaignlift.campaigns.dtos

import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import java.math.BigDecimal
import java.time.LocalDate

data class CampaignOwnerDetails(
    val id: Long,
    val createdBy: Long,
    val categoryId: Long,
    val categoryName: String,
    val title: String,
    val description: String,
    val goalAmount: BigDecimal,
    val interestRate: BigDecimal,
    val repaymentMonths: Int,
    val monthlyInstallment: BigDecimal,
    var amountRaised: BigDecimal,
    val bankFee: BigDecimal,
    val netToLenders: BigDecimal,
    val status: CampaignStatus,
    val submittedAt: LocalDate,
    val campaignDeadline: LocalDate,
    val imageUrl: String?,
    val accountId: Long
) : CampaignDetailResponse

fun CampaignEntity.toOwnerDetails(
    amountRaised: BigDecimal,
    monthlyInstallment: BigDecimal,
    bankFee: BigDecimal,
    netToLenders: BigDecimal
): CampaignOwnerDetails {
    return CampaignOwnerDetails(
        id = this.id!!,
        createdBy = this.createdBy!!,
        categoryId = this.category?.id!!,
        categoryName = this.category?.name!!,
        title = this.title,
        description = this.description!!,
        goalAmount = this.goalAmount!!,
        interestRate = this.interestRate,
        repaymentMonths = this.repaymentMonths,
        monthlyInstallment = monthlyInstallment,
        amountRaised = amountRaised,
        bankFee = bankFee,
        netToLenders = netToLenders,
        status = this.status,
        submittedAt = this.submittedAt!!,
        campaignDeadline = this.campaignDeadline!!,
        imageUrl = this.imageUrl,
        accountId = this.accountId!!
    )
}