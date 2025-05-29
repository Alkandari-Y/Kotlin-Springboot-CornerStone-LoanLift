package com.project.campaignlift.campaigns.dtos

data class CampaignTransactionHistoryResponse(
    val pledgeTransactions: List<CampaignTransactionViewDto>,
    val repaymentSummaries: List<MonthlyRepaymentSummaryDto>
)