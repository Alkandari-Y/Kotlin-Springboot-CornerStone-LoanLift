package com.project.banking.accounts.dtos

import com.project.banking.entities.AccountEntity
import com.project.banking.entities.TransactionEntity

data class TransactionResponse(
    val sourceAccount: AccountEntity,
    val destinationAccount: AccountEntity,
    val transaction: TransactionEntity,
    val category: String
)