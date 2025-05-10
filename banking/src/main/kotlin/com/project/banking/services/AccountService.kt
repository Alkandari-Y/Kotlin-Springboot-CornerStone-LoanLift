package com.project.banking.services

import com.project.banking.accounts.dtos.UpdateAccountRequest
import com.project.banking.entities.AccountEntity
import com.project.banking.repositories.projections.AccountView
import com.project.common.responses.authenthication.UserInfoDto

interface AccountService {
    fun getActiveAccountsByUserId(userId: Long): List<AccountView>
    fun createClientAccount(accountEntity: AccountEntity, userInfoDto: UserInfoDto): AccountEntity
    fun closeAccount(accountNumber: String, user: UserInfoDto): Unit
    fun updateAccount(
        accountNumber: String,
        userId: Long,
        accountUpdate: UpdateAccountRequest
    ): AccountEntity
    fun getAccountById(accountId: Long): AccountEntity?
    fun getByAccountNumber(accountNumber: String): AccountEntity?
    fun getAllAccountsByUserId(userId: Long): List<AccountEntity>
}