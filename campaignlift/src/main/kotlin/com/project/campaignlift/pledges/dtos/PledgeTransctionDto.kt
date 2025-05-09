package com.project.campaignlift.pledges.dtos

import com.project.campaignlift.entities.PledgeTransactionEntity
import com.project.campaignlift.entities.PledgeTransactionType

data class PledgeTransactionDto(
    val id: Long,
    val transactionId: Long,
    val type: PledgeTransactionType
)

fun PledgeTransactionEntity.toResultDto() = PledgeTransactionDto(
    id = id!!,
    transactionId = transactionId,
    type = type
)