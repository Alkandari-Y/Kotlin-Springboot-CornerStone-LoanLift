package com.project.campaignlift.pledges.dtos

import com.project.campaignlift.entities.PledgeEntity
import com.project.campaignlift.entities.PledgeStatus
import java.math.BigDecimal

data class PledgeWithPledgeTransactionsDto(
    val pledgeId: Long,
    val userId: Long,
    val accountId: Long,
    val amount: BigDecimal,
    val status: PledgeStatus,
    val transactions: List<PledgeTransactionDto>
)

fun PledgeEntity.toPledgeWithTransactionsDto(): PledgeWithPledgeTransactionsDto {
    return PledgeWithPledgeTransactionsDto(
        pledgeId = this.id!!,
        userId = this.userId,
        accountId = this.accountId,
        amount = this.amount,
        status = this.status,
        transactions = this.transactions.map {
            PledgeTransactionDto(
                id = it.id!!,
                transactionId = it.transactionId,
                type = it.type,
            )
        }
    )
}