package com.project.campaignlift.pledges.dtos

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CreatePledgeRequest(
    @field:NotNull(message = "Account ID is required.")
    @field:Min(value = 1, message = "Account ID must be greater than 0.")
    val accountId: Long?,

    @field:NotNull(message = "Campaign ID is required.")
    @field:Min(value = 1, message = "Campaign ID must be greater than 0.")
    val campaignId: Long?,

    @field:NotNull(message = "Amount is required.")
    @field:DecimalMin(value = "1000.000", message = "Amount must be at least 1000.000")
    val amount: BigDecimal?
)
