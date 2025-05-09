package com.project.campaignlift.pledges.dtos


data class PledgeResultDto(
    val pledge: UserPledgeDto,
    val transaction: PledgeTransactionDto
)
