package com.project.campaignlift.pledges.dtos

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class UpdatePledgeRequest(

    @field:NotNull(message = "Amount is required.")
    @field:DecimalMin(value = "1.000", message = "Amount must be at least 1.000")
    val amount: BigDecimal?

)
