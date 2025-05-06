package com.project.banking.services

import com.project.banking.accounts.dtos.TransactionResponse
import com.project.banking.accounts.dtos.TransferCreateRequest
import com.project.banking.accounts.exceptions.AccountNotFoundException
import com.project.banking.accounts.exceptions.InsufficientFundsException
import com.project.banking.accounts.exceptions.InvalidTransferException
import com.project.banking.entities.TransactionEntity
import com.project.banking.repositories.AccountRepository
import com.project.banking.repositories.TransactionRepository
import com.project.common.exceptions.ErrorCode
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class TransactionServiceImpl(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
): TransactionService {

    @Transactional
    override fun transfer(newTransaction: TransferCreateRequest, userIdMakingTransfer: Long): TransactionResponse {
        if (newTransaction.sourceAccountNumber == newTransaction.destinationAccountNumber) {
            throw InvalidTransferException(message="Cannot transfer to the same account.",  code = ErrorCode.INVALID_TRANSFER)
        }

        val sourceAccount = accountRepository.findByAccountNumber(newTransaction.sourceAccountNumber)
        val destinationAccount = accountRepository.findByAccountNumber(newTransaction.destinationAccountNumber)

        if (sourceAccount == null || destinationAccount == null) {
            throw AccountNotFoundException("One or both accounts not found.")
        }

        if (sourceAccount.isActive.not() || destinationAccount.isActive.not()) {
            throw InvalidTransferException(
                "Cannot transfer with inactive account.",
                code = ErrorCode.INVALID_ACCOUNT_TYPE
            )
        }

        if (sourceAccount.owner?.ownerId != userIdMakingTransfer) {
            throw InvalidTransferException("Cannot transfer with another persons account.", code = ErrorCode.INVALID_TRANSFER)
        }

        val newSourceBalance = sourceAccount.balance.setScale(3).subtract(newTransaction.amount)
        val newDestinationBalance = destinationAccount.balance.setScale(3).add(newTransaction.amount)

        if (newSourceBalance < BigDecimal.ZERO) {
            throw InsufficientFundsException("Transfer would result in a negative balance.")
        }

        val transaction = transactionRepository.save(
            TransactionEntity(
                sourceAccount = sourceAccount,
                destinationAccount = destinationAccount,
                amount = newTransaction.amount.setScale(3),
            )
        )

        val updatedSourceAccount = accountRepository.save(
            sourceAccount.copy(balance = newSourceBalance)
        )
        val updatedDestinationAccount = accountRepository.save(
            destinationAccount.copy(balance = newDestinationBalance)
        )
        return TransactionResponse(
            sourceAccount = updatedSourceAccount,
            destinationAccount = updatedDestinationAccount,
            transaction = transaction
        )
    }
}