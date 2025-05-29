package com.project.campaignlift.campaigns.dtos

import com.project.campaignlift.entities.PledgeTransactionType
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.YearMonth

data class MonthlyRepaymentSummaryDto(
    val month: YearMonth,
    val totalPaidWithBankFee: BigDecimal,
    val pledgeTransactionType: PledgeTransactionType = PledgeTransactionType.REPAYMENT,
)

interface MonthlyRepaymentSummaryProjection {
    fun getMonth(): Timestamp
    fun getTotalPaidWithBankFee(): BigDecimal
}
