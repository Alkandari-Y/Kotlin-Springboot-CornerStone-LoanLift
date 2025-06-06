package com.project.banking.transactions.dtos

import java.math.BigDecimal
import java.time.Instant

data class TransactionDetails(
    val sourceAccountNumber: String,
    val destinationAccountNumber: String,
    val amount: BigDecimal,
    val createdAt: Instant,
    val category: String
)
