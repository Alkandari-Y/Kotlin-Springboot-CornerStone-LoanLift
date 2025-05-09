package com.project.campaignlift.pledges.dtos

import com.project.campaignlift.entities.PledgeTransactionType
import com.project.common.enums.TransactionType
import java.math.BigDecimal
import java.time.Instant

data class PledgeTransactionWithDetails(
    val id: Long?,
    val type: PledgeTransactionType,
    val transactionId: Long,
    val amount: BigDecimal,
    val transactionType: TransactionType,
    val createdAt: Instant
)
