package com.project.banking.accounts

import com.project.banking.services.AccountService
import com.project.banking.services.TransactionService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/accounts/v1/accounts")
class AccountsControllers(
    private val accountService: AccountService,
    private val transactionService: TransactionService
) {
//    @GetMapping
//    fun getAllAccounts(
//        @AuthenticationPrincipal user: UserDetails
//        ): List<AccountListItemProjection> {
//        return accountService.getAccountByUserId()
//    }

//    @PostMapping
//    fun createAccount(
//        @Valid @RequestBody accountCreateRequestDto : AccountCreateRequest,
//    ) : ResponseEntity<Any>
//    {
//        val account = accountService.createAccount(
//            accountCreateRequestDto.toEntity()
//        )
//        return ResponseEntity(account, HttpStatus.CREATED)
//    }

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