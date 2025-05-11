package com.project.campaignlift.admin.dtos

import com.project.campaignlift.entities.CampaignStatus
import com.project.campaignlift.entities.PledgeTransactionType
import java.math.BigDecimal
import java.time.LocalDate

data class CampaignTransactionDetailsDto(
    val sourceAccountId: Long,
    val sourceAccountNumber: String,
    val destinationAccountId: Long,
    val destinationAccountNumber: String,
    val amount: BigDecimal,
    val transactionDate: LocalDate,
    val transactionType: PledgeTransactionType,
    val pledgeId: Long
)

data class CampaignWithTransactionsListAdminDto(
    val id: Long,
    val createdBy: Long,
    val accountId: Long,
    val title: String,
    val goalAmount: BigDecimal,
    val interestRate: BigDecimal,
    val repaymentMonths: Int,
    val status: CampaignStatus,
    val transactions: List<CampaignTransactionDetailsDto>
)
