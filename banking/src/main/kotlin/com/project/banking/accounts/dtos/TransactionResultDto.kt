package com.project.banking.accounts.dtos

data class TransactionResultDto(
    val sourceAccount: AccountEntity,
    val destinationAccount: AccountEntity,
    val transaction: TransactionEntity,
)