package com.project.banking.services

import com.project.banking.accounts.dtos.TransactionResponse
import com.project.banking.accounts.dtos.TransferCreateRequest
import com.project.banking.transactions.dtos.TransactionDetails

interface TransactionService {
    fun transfer(
        newTransaction: TransferCreateRequest,
        userIdMakingTransfer: Long
    ): TransactionResponse

    fun getTransactionsByAccount(accountNumber: String): List<TransactionDetails>
    fun getAllTransactionByUserId(
        userId: Long
    ): List<TransactionDetails>
}