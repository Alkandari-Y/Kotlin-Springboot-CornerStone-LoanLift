package com.project.common.responses.banking

import java.math.BigDecimal

data class AccountResponse(
    val id: Long,
    val accountNumber: String,
    val name: String,
    val balance: BigDecimal,
    val active: Boolean,
    val ownerId: Long
)
