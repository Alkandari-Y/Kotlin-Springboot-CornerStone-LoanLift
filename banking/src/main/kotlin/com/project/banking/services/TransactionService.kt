package com.project.banking.services

import com.project.banking.accounts.dtos.TransactionResultDto
import com.project.banking.accounts.dtos.TransferCreateRequestDto


interface TransactionService {
    fun transfer(
        newTransaction: TransferCreateRequestDto,
        userIdMakingTransfer: Long
    ): TransactionResultDto
}