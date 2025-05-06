package com.project.banking.services

import com.project.banking.entities.AccountEntity
import com.project.banking.entities.projections.AccountListItemProjection
import com.project.common.responses.authenthication.UserInfoDto

interface AccountService {
    fun getAllAccounts(): List<AccountListItemProjection>
    fun getAccountByUserId(userId: Long): List<AccountListItemProjection>
    fun createClientAccount(accountEntity: AccountEntity, userInfoDto: UserInfoDto): AccountEntity
    fun closeAccount(accountNumber: String, userId: Long): Unit
}