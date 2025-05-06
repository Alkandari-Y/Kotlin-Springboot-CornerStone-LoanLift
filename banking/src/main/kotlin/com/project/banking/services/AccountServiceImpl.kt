package com.project.banking.services


import com.project.banking.accounts.dtos.UpdateAccountRequest
import com.project.banking.accounts.exceptions.AccountLimitException
import com.project.banking.accounts.exceptions.AccountNotFoundException
import com.project.banking.accounts.exceptions.AccountVerificationException
import com.project.banking.entities.AccountEntity
import com.project.banking.entities.AccountOwnerType
import com.project.banking.entities.AccountOwnershipEntity
import com.project.banking.entities.projections.AccountListItemProjection
import com.project.banking.repositories.AccountOwnershipRepository
import com.project.banking.repositories.AccountRepository
import com.project.common.exceptions.APIException
import com.project.common.exceptions.ErrorCode
import com.project.common.responses.authenthication.UserInfoDto
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
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
            if (this.isDeleted) {
                throw APIException("Account was deleted")
            }
            if (this.isActive) {
                accountRepository.save(this.copy(isActive = false))
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

        if (accountToUpdate.owner?.ownerId != userId) {
            throw AccountVerificationException("Only owners can update accounts")
        }
        if (accountToUpdate.isDeleted) {
            throw APIException("Account was deleted")
        }
        if (!accountToUpdate.isActive) {
            throw APIException("Account is not active")
        }

        val updatedAccount = accountToUpdate.copy(
            name = accountToUpdate.name,
        )

        if (accountUpdate.asPrimary == true) {
            val currentOwnership = accountToUpdate.owner ?: throw APIException("Ownership information is missing")

            val allOwned = accountOwnerRepository.findAllByOwnerIdAndOwnerType(userId, AccountOwnerType.USER)
            allOwned
                .filter { it.isPrimary && it.account?.accountNumber != accountNumber }
                .forEach {
                    accountOwnerRepository.save(it.copy(isPrimary = false))
                }
            val updatedOwnership = currentOwnership.copy(isPrimary = true)
            accountOwnerRepository.save(updatedOwnership)
        }
        return accountRepository.save(updatedAccount)
    }

    override fun getByAccountNumber(accountNumber: String): AccountEntity? {
        val account = accountRepository.findByAccountNumber(accountNumber)
            ?: return null

        return when {
            !account.isDeleted && account.isActive -> account
            account.isDeleted || !account.isActive ->
                throw APIException(
                    message = "Account was deleted or is not active anymore",
                    code = ErrorCode.ACCOUNT_NOT_ACTIVE,
                    httpStatus = HttpStatus.BAD_REQUEST
                )
            else -> null
        }
    }

}