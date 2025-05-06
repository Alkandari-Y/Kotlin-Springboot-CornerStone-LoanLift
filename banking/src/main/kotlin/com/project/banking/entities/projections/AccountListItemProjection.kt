package com.project.banking.entities.projections

import com.project.banking.entities.AccountOwnerType
import java.math.BigDecimal

interface AccountListItemProjection {
    val id: Long
    val name: String
    val accountNumber: String
    val balance: BigDecimal
    val isActive: Boolean
    val isDeleted: Boolean
    val category: CategoryItemProjection
    val owner: AccountOwnershipProjection
}

interface CategoryItemProjection {
    val id: Long
    val name: String
}

interface AccountOwnershipProjection {
    val id: Long
    val ownerId: Long
    val ownerType: AccountOwnerType
}