package com.project.banking.accounts.dtos

import java.math.BigDecimal

data class UpdatedBalanceResponse(
    val newBalance: BigDecimal
)

fun TransactionResultDto.toUpdatedBalanceResponse() = UpdatedBalanceResponse(
    newBalance = this.sourceAccount.balance.setScale(3)
)