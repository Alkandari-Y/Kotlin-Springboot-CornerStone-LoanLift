package com.project.campaignlift.pledges.dtos


import com.project.campaignlift.entities.PledgeEntity
import com.project.campaignlift.entities.PledgeStatus
import java.math.BigDecimal
import java.time.LocalDate

data class UserPledgeDto(
    val id: Long,
    val amount: BigDecimal,
    val status: PledgeStatus,
    val campaignTitle: String,
    val campaignId: Long,
    val createdAt: LocalDate
)

fun PledgeEntity.toUserPledgeDto(title: String, campaignId: Long) = UserPledgeDto(
    id = id!!,
    amount = amount,
    status = status,
    campaignTitle = title,
    campaignId = campaignId,
    createdAt = createdAt
)