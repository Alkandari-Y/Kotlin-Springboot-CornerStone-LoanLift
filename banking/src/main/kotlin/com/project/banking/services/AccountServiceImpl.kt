package com.project.banking.services


import com.project.banking.accounts.exceptions.AccountLimitException
import com.project.banking.accounts.exceptions.AccountVerificationException
import com.project.banking.entities.AccountEntity
import com.project.banking.entities.AccountOwnerType
import com.project.banking.entities.AccountOwnershipEntity
import com.project.banking.entities.projections.AccountListItemProjection
import com.project.banking.repositories.AccountOwnershipRepository
import com.project.banking.repositories.AccountRepository
import com.project.common.responses.authenthication.UserInfoDto
import org.springframework.stereotype.Service

const val MAX_ACCOUNT_LIMIT = 3

@Service
class AccountServiceImpl(
    private val accountRepository: AccountRepository,
    private val accountOwnerRepository: AccountOwnershipRepository,
): AccountService {
    override fun getAllAccounts(): List<AccountListItemProjection> {
        return accountRepository.allAccounts()
    }

    override fun getAccountByUserId(userId: Long): List<AccountListItemProjection> {
        return accountRepository.findAllByOwner(userId, AccountOwnerType.USER)
    }

    override fun createClientAccount(
        accountEntity: AccountEntity,
        userInfoDto: UserInfoDto)
    : AccountEntity {

        val numOfCustomerAccount = accountRepository.countByOwner(
            userId = userInfoDto.userId,
            ownerType = AccountOwnerType.USER
        )

        if (numOfCustomerAccount >= MAX_ACCOUNT_LIMIT) {
            throw AccountLimitException()
        }

        val newAccount = accountRepository.save(accountEntity)
        accountOwnerRepository.save(
            AccountOwnershipEntity(
                ownerId = userInfoDto.userId,
                ownerType = AccountOwnerType.USER,
                account = newAccount,
                isPrimary = numOfCustomerAccount == 0L,
            )
        )
        return newAccount
    }

    override fun closeAccount(accountNumber: String, userId: Long) {
        accountRepository.findByAccountNumber(accountNumber)?.apply {
            if (this.owner?.ownerId != userId) {
                throw AccountVerificationException("Only owners can close accounts")
            }
            if (this.isActive) {
                accountRepository.save(this.copy(isActive = false))
            }
        }
    }
}