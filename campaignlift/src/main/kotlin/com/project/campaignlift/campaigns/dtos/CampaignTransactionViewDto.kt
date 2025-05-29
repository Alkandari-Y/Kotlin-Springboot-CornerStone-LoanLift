package com.project.campaignlift.campaigns.dtos

import com.project.campaignlift.entities.PledgeTransactionType
import com.project.common.enums.TransactionType
import java.math.BigDecimal
import java.time.Instant

data class CampaignTransactionViewDto(
    val transactionId: Long,
    val transactionType: TransactionType,
    val pledgeTransactionType: PledgeTransactionType,
    val amount: BigDecimal,
    val createdAt: Instant,
)
