package com.project.banking.entities.projections

import java.math.BigDecimal

interface AccountListItemProjection {
    val id: Long
    val name: String
    val userId: Long
    val balance: BigDecimal
    val isPrimary: Boolean
    val isActive: Boolean
    val isDeleted: Boolean
    val accountNumber: String
    val category: CategoryItemProjection
}

interface CategoryItemProjection {
    val id: Long
    val name: String
}