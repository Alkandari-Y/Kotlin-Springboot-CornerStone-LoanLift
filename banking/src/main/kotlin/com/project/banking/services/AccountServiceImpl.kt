package com.project.banking.services

import com.project.banking.accounts.dtos.AccountResponse
import com.project.banking.accounts.dtos.UpdateAccountRequest
import com.project.banking.accounts.dtos.toBasicResponse
import com.project.banking.accounts.exceptions.AccountLimitException
import com.project.banking.accounts.exceptions.AccountNotFoundException
import com.project.banking.accounts.exceptions.AccountVerificationException
import com.project.banking.entities.AccountEntity
import com.project.banking.repositories.AccountRepository
import com.project.common.exceptions.APIException
import com.project.common.responses.authenthication.UserInfoDto
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

const val MAX_ACCOUNT_LIMIT = 3

@Service
class AccountServiceImpl(
    private val accountRepository: AccountRepository,
): AccountService {
    override fun getActiveAccountsByUserId(userId: Long): List<AccountResponse> {
        return accountRepository.findAllActiveByOwnerId(userId)
    }

    override fun createClientAccount(
        accountEntity: AccountEntity,
        userInfoDto: UserInfoDto)
    : AccountEntity {

        val numOfCustomerAccount = accountRepository.getAccountCountByUserId(
            userId = userInfoDto.userId,
        )

        if (numOfCustomerAccount >= MAX_ACCOUNT_LIMIT) {
            throw AccountLimitException()
        }

        return accountRepository.save(accountEntity.copy(ownerId = userInfoDto.userId))
    }

    override fun closeAccount(accountNumber: String, userId: Long) {
        accountRepository.findByAccountNumber(accountNumber)?.apply {
            if (this.ownerId != userId) {
                throw AccountVerificationException("Only owners can close accounts")
            }
            if (this.active) {
                accountRepository.save(this.copy(active = false))
            }
        }
    }

    @Transactional
    override fun updateAccount(
        accountNumber: String,
        userId: Long,
        accountUpdate: UpdateAccountRequest
    ): AccountEntity {
        val accountToUpdate = accountRepository.findByAccountNumber(accountNumber)
            ?: throw AccountNotFoundException("Account not found")


        if (!accountToUpdate.active) {
            throw APIException("Account is not active")
        }

        val updatedAccount =accountRepository.save(
            accountToUpdate.copy(
                name = accountUpdate.name
            )
        )

        return updatedAccount
    }

    override fun getByAccountNumber(accountNumber: String): AccountEntity? {
        return accountRepository.findByAccountNumber(accountNumber)
    }


    override fun getAllAccountsByUserId(userId: Long): List<AccountEntity> {
        return accountRepository.findAllByOwnerId(userId)
    }
}