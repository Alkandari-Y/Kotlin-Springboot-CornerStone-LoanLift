package com.project.campaignlift.pledges.dtos

import com.project.campaignlift.entities.PledgeEntity
import com.project.campaignlift.entities.PledgeStatus
import java.math.BigDecimal
import java.time.LocalDate

data class PledgeOverviewDto(
    val id: Long,
    val userId: Long,
    val accountId: Long,
    val amount: BigDecimal,
    val createdAt: LocalDate,
    val updatedAt: LocalDate,
    val status: PledgeStatus,
    val withdrawnAt: LocalDate?,
)

fun PledgeEntity.toPledgeOverviewDto() = PledgeOverviewDto(
    id = this.id!!,
    userId = this.userId,
    accountId = this.accountId,
    amount = this.amount,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    status = this.status,
    withdrawnAt = this.withdrawnAt
)