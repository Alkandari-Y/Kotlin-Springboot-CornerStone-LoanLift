package com.project.banking.accounts

import com.project.banking.accounts.dtos.AccountCreateRequest
import com.project.banking.accounts.dtos.toEntity
import com.project.banking.entities.AccountEntity
import com.project.banking.entities.projections.AccountListItemProjection
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

//    @PostMapping(path=["/transfer"])
//    fun transfer(
//        @Valid @RequestBody transferCreateRequestDto: TransferCreateRequest,
//    ): ResponseEntity<UpdatedBalanceResponse> {
//            val result = transactionService.transfer(
//                transferCreateRequestDto,
//                userIdMakingTransfer =
//            )
//            return ResponseEntity(
//                result.toUpdatedBalanceResponse(),
//                HttpStatus.OK
//            )
//    }

//    @PostMapping(path=["/{accountNumber}/close"])
//    fun closeAccount(
//        @PathVariable accountNumber : String,
//    ) {
//        accountService.closeAccount(accountNumber )
//    }
}