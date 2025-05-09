package com.project.campaignlift.services

import com.project.campaignlift.entities.projections.UserPledgeProjection
import com.project.campaignlift.pledges.dtos.PledgeResultDto
import com.project.campaignlift.pledges.dtos.UserPledgeDto
import com.project.common.responses.authenthication.UserInfoDto

import java.math.BigDecimal

interface PledgeService {

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
