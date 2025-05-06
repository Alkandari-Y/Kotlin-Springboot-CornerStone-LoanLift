package com.project.banking.services

import com.project.banking.entities.AccountEntity
import com.project.banking.entities.projections.AccountListItemProjection


interface AccountService {
    fun getAllAccounts(): List<AccountListItemProjection>
    fun getAccountByUserId(userId: Long): List<AccountListItemProjection>
    fun createAccount(accountEntity: AccountEntity): AccountEntity
    fun closeAccount(accountNumber: String, userId: Long): Unit
}