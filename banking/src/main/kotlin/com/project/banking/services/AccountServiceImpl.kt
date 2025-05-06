package com.project.banking.services

import com.coded.bankingproject.accounts.exceptions.AccountLimitException
import com.coded.bankingproject.accounts.exceptions.AccountVerificationException
import com.coded.bankingproject.domain.entities.AccountEntity
import com.coded.bankingproject.domain.entities.UserEntity
import com.coded.bankingproject.domain.projections.AccountListItemProjection
import com.coded.bankingproject.repositories.AccountRepository
import org.springframework.stereotype.Service

const val MAX_ACCOUNT_LIMIT = 3

@Service
class AccountServiceImpl(
    private val accountRepository: AccountRepository,
): AccountService {
    override fun getAllAccounts(): List<AccountListItemProjection> {
        return accountRepository.allAccounts()
    }

    override fun getAccountByUserId(userId: Long): List<AccountListItemProjection> {
        return accountRepository.findByUserId(userId)
    }

    override fun createAccount(accountEntity: AccountEntity): AccountEntity {
        val user = accountEntity.user ?: throw IllegalArgumentException("User is required")
        val userId = user.id ?: throw IllegalArgumentException("User ID is required")

        val numOfCustomerAccount = accountRepository.getAccountCountByUserId(userId)

        if (numOfCustomerAccount >= MAX_ACCOUNT_LIMIT) {
            throw AccountLimitException()
        }
        return accountRepository.save(accountEntity)
    }

    override fun closeAccount(accountNumber: String, user: UserEntity) {
        accountRepository.findByAccountNumber(accountNumber)?.apply {
            if (this.user != user) {
                throw AccountVerificationException("Only owners can close accounts")
            }
            if (this.isActive) {
                accountRepository.save(this.copy(isActive = false))
            }
        }
    }
}