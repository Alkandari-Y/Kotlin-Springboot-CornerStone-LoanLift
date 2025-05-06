package com.project.banking.accounts

import com.project.banking.accounts.dtos.AccountCreateRequest
import com.project.banking.accounts.dtos.AccountResponse
import com.project.banking.accounts.dtos.TransferCreateRequest
import com.project.banking.accounts.dtos.UpdateAccountRequest
import com.project.banking.accounts.dtos.UpdatedBalanceResponse
import com.project.banking.accounts.dtos.toBasicResponse
import com.project.banking.accounts.dtos.toEntity
import com.project.banking.accounts.dtos.toUpdatedBalanceResponse
import com.project.banking.accounts.exceptions.AccountNotFoundException
import com.project.banking.entities.AccountEntity
import com.project.banking.entities.projections.AccountListItemProjection
import com.project.banking.services.AccountOwnershipService
import com.project.banking.services.AccountService
import com.project.banking.services.TransactionService
import com.project.common.exceptions.APIException
import com.project.common.exceptions.ErrorCode
import com.project.common.responses.authenthication.UserInfoDto
import com.project.common.security.RemoteUserPrincipal
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/accounts")
class AccountsControllers(
    private val accountService: AccountService,
    private val accountOwnershipService: AccountOwnershipService,
    private val transactionService: TransactionService
) {

    @GetMapping
    fun getAllAccounts(
        @AuthenticationPrincipal user: RemoteUserPrincipal
    ): List<AccountListItemProjection> {
        return accountService.getAccountByUserId(user.getUserId())
    }
    @PostMapping
    fun createAccount(
        @Valid @RequestBody accountCreateRequestDto : AccountCreateRequest,
        @RequestAttribute("authUser") authUser: UserInfoDto,
    ) : ResponseEntity<AccountEntity>
    {
        if (authUser.isActive.not()) {
            throw APIException(
                message = "User must complete KYC registration",
                httpStatus = HttpStatus.BAD_REQUEST,
                code = ErrorCode.INCOMPLETE_USER_REGISTRATION,
                cause = null
            )
        }
        val account = accountService.createClientAccount(
            accountCreateRequestDto.toEntity(),
            authUser,
        )
        return ResponseEntity(account, HttpStatus.CREATED)
    }

    @PostMapping(path=["/transfer"])
    fun transfer(
        @Valid @RequestBody transferCreateRequestDto: TransferCreateRequest,
        @RequestAttribute("authUser") authUser: UserInfoDto,

        ): ResponseEntity<UpdatedBalanceResponse> {
            val result = transactionService.transfer(
                transferCreateRequestDto,
                userIdMakingTransfer = authUser.userId,
            )
            return ResponseEntity(
                result.toUpdatedBalanceResponse(),
                HttpStatus.OK
            )
    }

    @PostMapping(path=["/close/{accountNumber}"])
    fun closeAccount(
        @PathVariable accountNumber : String,
        @RequestAttribute("authUser") authUser: UserInfoDto,
        ): ResponseEntity<Unit> {
        accountService.closeAccount(accountNumber, authUser.userId)
        return ResponseEntity(HttpStatus.OK)
    }

    @PutMapping(path=["/details/{accountNumber}"])
    fun updateAccount(
        @PathVariable accountNumber : String,
        @Valid @RequestBody accountUpdate: UpdateAccountRequest,
        @RequestAttribute("authUser") authUser: UserInfoDto,
    ): ResponseEntity<AccountResponse>{
        val updatedAccount = accountService.updateAccount(
            accountNumber = accountNumber,
            userId = authUser.userId,
            accountUpdate = accountUpdate,
        ).toBasicResponse()
        return ResponseEntity(updatedAccount, HttpStatus.OK)
    }

    @GetMapping(path=["/details/{accountNumber}"])
    fun getAccountDetails(
        @PathVariable accountNumber : String,
        @AuthenticationPrincipal user: RemoteUserPrincipal
    ): ResponseEntity<AccountResponse>{
        val accountOwned = accountOwnershipService.getByAccountNumber(accountNumber)
            ?: throw AccountNotFoundException()

        val isOwner = accountOwned.ownerId == user.getUserId()
        val isAdmin = user.authorities.any { it.authority == "ROLE_ADMIN" }

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val account = accountOwned.account ?: throw AccountNotFoundException()

        if (account.isDeleted || account.isActive.not()) {
            throw APIException(
                message = "Account was deleted or is not active anymore",
                httpStatus = HttpStatus.BAD_REQUEST,
                code = ErrorCode.ACCOUNT_NOT_ACTIVE,
            )
        }

        return ResponseEntity(accountOwned.account?.toBasicResponse(), HttpStatus.OK)
    }
}