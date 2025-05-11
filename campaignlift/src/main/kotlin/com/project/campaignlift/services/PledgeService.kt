package com.project.campaignlift.services

import com.project.campaignlift.entities.PledgeEntity
import com.project.campaignlift.pledges.dtos.PledgeResultDto
import com.project.campaignlift.pledges.dtos.PledgeTransactionWithDetails
import com.project.campaignlift.pledges.dtos.UserPledgeDto
import com.project.common.responses.authenthication.UserInfoDto

import java.math.BigDecimal

interface PledgeService {
    fun getAmountRaised(campaignId: Long): BigDecimal

    fun getPledgeTransactions(pledgeId: Long): List<PledgeTransactionWithDetails>

    fun getPledgeDetails(pledgeId: Long): PledgeEntity

    fun getAllUserPledges(userId: Long): List<UserPledgeDto>

    fun createPledge(
        userInfo: UserInfoDto,
        accountId: Long,
        campaignId: Long,
        amount: BigDecimal
    ): PledgeResultDto

    fun updatePledge(
        pledgeId: Long,
        userId: Long,
        newAmount: BigDecimal
    ): PledgeResultDto

    fun withdrawPledge(pledgeId: Long, userId: Long)

}
