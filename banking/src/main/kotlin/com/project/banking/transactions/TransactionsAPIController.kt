package com.project.banking.transactions

import com.project.banking.accounts.exceptions.AccountNotFoundException
import com.project.banking.services.AccountOwnershipService
import com.project.banking.services.AccountService
import com.project.banking.services.TransactionService
import com.project.banking.transactions.dtos.TransactionDetails
import com.project.common.security.RemoteUserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/transactions")
class TransactionsAPIController(
    private val accountOwnershipRepository: AccountOwnershipService,
    private val transactionService: TransactionService,
    private val accountService: AccountService,
){


    @GetMapping("/account/{accountNumber}")
    fun getAllTransactionsByAccountNumber(
        @PathVariable("accountNumber") accountNumber: String,
        @AuthenticationPrincipal user: RemoteUserPrincipal
    ): ResponseEntity<List<TransactionDetails>> {
        val account = accountOwnershipRepository.getByAccountNumber(accountNumber)
            ?: throw AccountNotFoundException()

        val isOwner = account.ownerId == user.getUserId()
        val isAdmin = user.authorities.any { it.authority == "ROLE_ADMIN" }

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val transactions = transactionService.getTransactionsByAccount(account)
        return ResponseEntity(transactions, HttpStatus.OK)
    }

}