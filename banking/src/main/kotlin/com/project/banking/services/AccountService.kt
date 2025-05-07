package com.project.banking.services

import com.project.banking.accounts.dtos.AccountResponse
import com.project.banking.accounts.dtos.UpdateAccountRequest
import com.project.banking.entities.AccountEntity
import com.project.banking.entities.projections.AccountListItemProjection
import com.project.common.responses.authenthication.UserInfoDto

interface AccountService {
    fun getAccountByUserId(userId: Long): List<AccountResponse>
    fun createClientAccount(accountEntity: AccountEntity, userInfoDto: UserInfoDto): AccountEntity
    fun closeAccount(accountNumber: String, userId: Long): Unit
    fun updateAccount(
        accountNumber: String,
        userId: Long,
        accountUpdate: UpdateAccountRequest
    ): AccountEntity

    fun getByAccountNumber(accountNumber: String): AccountEntity?
}