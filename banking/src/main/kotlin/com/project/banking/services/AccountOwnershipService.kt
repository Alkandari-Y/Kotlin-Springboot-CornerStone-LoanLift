package com.project.banking.services

import com.project.banking.entities.AccountOwnershipEntity

interface AccountOwnershipService {
    fun getByAccountNumber(accountNumber: String): AccountOwnershipEntity?
}