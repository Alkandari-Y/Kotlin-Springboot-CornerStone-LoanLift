package com.project.banking.repositories

import com.project.banking.entities.AccountEntity
import com.project.banking.entities.AccountOwnerType
import com.project.banking.entities.projections.AccountListItemProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository: JpaRepository<AccountEntity, Long> {
    @Query("SELECT a FROM AccountEntity a WHERE a.isActive = TRUE AND a.isDeleted = FALSE")
    fun allAccounts(): List<AccountListItemProjection>

    fun findByAccountNumber(accountNumber: String): AccountEntity?

    @Query("""
    SELECT a FROM AccountEntity a
    JOIN a.owner ao
    WHERE ao.ownerId = :userId AND ao.ownerType = :ownerType
""")
    fun findAllByOwner(
        @Param("userId") userId: Long,
        @Param("ownerType") ownerType: AccountOwnerType = AccountOwnerType.USER
    ): List<AccountListItemProjection>

    @Query("""
    SELECT a FROM AccountEntity a
    JOIN a.owner ao
    WHERE a.id = :accountId AND ao.ownerId = :userId AND ao.ownerType = :ownerType
""")
    fun findByIdAndOwner(
        @Param("accountId") accountId: Long,
        @Param("userId") userId: Long,
        @Param("ownerType") ownerType: AccountOwnerType = AccountOwnerType.USER
    ): AccountEntity?

    @Query("""
    SELECT COUNT(a) FROM AccountEntity a
    JOIN a.owner ao
    WHERE ao.ownerId = :userId AND ao.ownerType = :ownerType
""")
    fun countByOwner(
        @Param("userId") userId: Long,
        @Param("ownerType") ownerType: AccountOwnerType = AccountOwnerType.USER
    ): Long

    @Query("""
    SELECT a FROM AccountEntity a
    JOIN a.owner ao
    WHERE ao.ownerId = :userId 
    AND ao.ownerType = :ownerType
    AND a.isActive = true AND a.isDeleted = false
""")
    fun findActiveByOwner(
        @Param("userId") userId: Long,
        @Param("ownerType") ownerType: AccountOwnerType = AccountOwnerType.USER
    ): List<AccountListItemProjection>




}