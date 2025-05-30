package com.project.campaignlift.pledges.dtos


import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.campaignlift.entities.PledgeEntity
import com.project.campaignlift.entities.PledgeStatus
import com.project.common.exceptions.APIException
import java.math.BigDecimal
import java.time.LocalDate

data class UserPledgeDto(
    val id: Long,
    val amount: BigDecimal,
    val status: PledgeStatus,
    val campaignTitle: String,
    val campaignId: Long,
    val createdAt: LocalDate,
    val campaignImage: String,
    val interestRate: BigDecimal,
    val campaignStatus: CampaignStatus,
)

fun PledgeEntity.toUserPledgeDto(title: String, campaign: CampaignEntity) = UserPledgeDto(
    id = id!!,
    amount = amount,
    status = status,
    campaignTitle = title,
    campaignId = campaign.id ?: throw APIException("campaign id is missing"),
    campaignImage = campaign.imageUrl ?: throw APIException("campaign image url is missing"),
    createdAt = createdAt,
    interestRate = campaign.interestRate,
    campaignStatus = campaign.status,
)