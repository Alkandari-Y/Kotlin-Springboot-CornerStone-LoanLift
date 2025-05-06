package com.project.banking.accounts

import com.project.banking.accounts.dtos.AccountCreateRequest
import com.project.banking.accounts.dtos.TransferCreateRequestDto
import com.project.banking.accounts.dtos.UpdatedBalanceResponse
import com.project.banking.accounts.dtos.toEntity
import com.project.banking.accounts.dtos.toUpdatedBalanceResponse
import com.project.banking.entities.projections.AccountListItemProjection
import com.project.banking.services.AccountService
import com.project.banking.services.TransactionService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/accounts/v1/accounts")
class AccountsControllers(
    private val accountService: AccountService,
    private val transactionService: TransactionService
) {
    @GetMapping
    fun getAllAccounts(
        @AuthenticationPrincipal user: UserDetails
        ): List<AccountListItemProjection> {
        return accountService.getAccountByUserId()
    }

    @PostMapping
    fun createAccount(
        @Valid @RequestBody accountCreateRequestDto : AccountCreateRequest,
    ) : ResponseEntity<Any>
    {
        val account = accountService.createAccount(
            accountCreateRequestDto.toEntity()
        )
        return ResponseEntity(account, HttpStatus.CREATED)
    }

    @PostMapping(path=["/transfer"])
    fun transfer(
        @Valid @RequestBody transferCreateRequestDto: TransferCreateRequestDto,
    ): ResponseEntity<UpdatedBalanceResponse> {
            val result = transactionService.transfer(
                transferCreateRequestDto,
                userIdMakingTransfer =
            )
            return ResponseEntity(result.toUpdatedBalanceResponse(), HttpStatus.OK)
    }

    @PostMapping(path=["/{accountNumber}/close"])
    fun closeAccount(
        @PathVariable accountNumber : String,
    ) {
        accountService.closeAccount(accountNumber )
    }
}