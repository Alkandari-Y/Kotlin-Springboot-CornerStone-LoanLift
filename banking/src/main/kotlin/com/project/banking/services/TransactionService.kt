package com.project.banking.services

import com.project.banking.accounts.dtos.TransactionResponse
import com.project.banking.accounts.dtos.TransferCreateRequest

interface TransactionService {
    fun transfer(
        newTransaction: TransferCreateRequest,
        userIdMakingTransfer: Long
    ): TransactionResponse
}