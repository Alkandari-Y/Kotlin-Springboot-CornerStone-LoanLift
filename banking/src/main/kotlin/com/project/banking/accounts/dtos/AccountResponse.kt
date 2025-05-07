package com.project.banking.accounts.dtos

import com.project.banking.entities.AccountEntity
import java.math.BigDecimal

data class AccountResponse(
    val id: Long,
    val accountNumber: String,
    val name: String,
    val balance: BigDecimal,
    val active: Boolean,
    val ownerId: Long
)

fun AccountEntity.toBasicResponse() = AccountResponse(
    id = id!!,
    accountNumber = accountNumber,
    name = name,
    balance = balance,
    ownerId = ownerId!!,
    active = active
)