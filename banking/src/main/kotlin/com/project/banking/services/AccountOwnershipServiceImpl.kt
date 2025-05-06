package com.project.banking.services

import com.project.banking.entities.AccountOwnershipEntity
import com.project.banking.repositories.AccountOwnershipRepository
import org.springframework.stereotype.Service


@Service
class AccountOwnershipServiceImpl(
    private val accountOwnershipRepository: AccountOwnershipRepository
): AccountOwnershipService{
    override fun getByAccountNumber(accountNumber: String): AccountOwnershipEntity? {
        return accountOwnershipRepository.findByAccountNumber(accountNumber)
    }
}