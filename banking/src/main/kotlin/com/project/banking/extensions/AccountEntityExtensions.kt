package com.project.banking.extensions

import com.project.banking.entities.AccountEntity
import com.project.common.responses.banking.AccountResponse

fun AccountEntity.toBasicResponse() = AccountResponse(
    id = id!!,
    accountNumber = accountNumber,
    name = name,
    balance = balance,
    ownerId = ownerId!!,
    active = active,
    ownerType = ownerType
)