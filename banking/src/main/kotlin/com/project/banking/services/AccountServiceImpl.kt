package com.project.banking.services

import com.project.banking.accounts.dtos.UpdateAccountRequest
import com.project.common.exceptions.accounts.AccountLimitException
import com.project.common.exceptions.accounts.AccountNotFoundException
import com.project.common.exceptions.accounts.AccountVerificationException
import com.project.banking.entities.AccountEntity
import com.project.banking.repositories.AccountRepository
import com.project.banking.repositories.projections.AccountView
import com.project.common.enums.AccountType
import com.project.common.exceptions.accounts.AccountNotActiveException
import com.project.common.responses.authenthication.UserInfoDto
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

const val MAX_ACCOUNT_LIMIT = 3

@Service
class AccountServiceImpl(
    private val accountRepository: AccountRepository,
    private val mailService: MailService
): AccountService {
    override fun getActiveAccountsByUserId(userId: Long): List<AccountView> {
        return accountRepository.findByOwnerIdActive(userId)
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

        val account = accountRepository.save(accountEntity.copy(ownerId = userInfoDto.userId))
        mailService.sendHtmlEmail(
            to = userInfoDto.username,
            subject = "Your bank account has been created",
            username = userInfoDto.username,
            bodyText = "Your account has been successfully created."
        )
        return account
    }

    override fun closeAccount(accountNumber: String, user: UserInfoDto) {
        accountRepository.findByAccountNumber(accountNumber)?.apply {
            if (this.ownerId != user.userId) {
                throw AccountVerificationException()
            }
            if (this.ownerType != AccountType.USER) {
                throw AccountVerificationException("Only user account can be closed.")
            }
            if (this.active) {
                accountRepository.save(this.copy(active = false))
            }
            mailService.sendHtmlEmail(
                to = user.email,
                subject = "Your bank account has been closed",
                username = user.username,
                bodyText = "Your account has been successfully closed"
            )
        }
    }

    @Transactional
    override fun updateAccount(
        accountNumber: String,
        userId: Long,
        accountUpdate: UpdateAccountRequest
    ): AccountEntity {
        val accountToUpdate = accountRepository.findByAccountNumber(accountNumber)
            ?: throw AccountNotFoundException()


        if (!accountToUpdate.active) {
            throw AccountNotActiveException(accountNumber)
        }

        val updatedAccount =accountRepository.save(
            accountToUpdate.copy(
                name = accountUpdate.name
            )
        )

        return updatedAccount
    }

    override fun getAccountById(accountId: Long): AccountEntity? {
        return accountRepository.findByIdOrNull(accountId)
    }

    override fun getByAccountNumber(accountNumber: String): AccountEntity? {
        return accountRepository.findByAccountNumber(accountNumber)
    }


    override fun getAllAccountsByUserId(userId: Long): List<AccountEntity> {
        return accountRepository.findAllByOwnerId(userId)
    }
}