package com.project.banking.repositories

import com.project.banking.entities.AccountEntity
import com.project.banking.entities.projections.AccountListItemProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository: JpaRepository<AccountEntity, Long> {
    @Query("SELECT a FROM AccountEntity a WHERE a.isActive = TRUE")
    fun allAccounts(): List<AccountListItemProjection>

    fun findByAccountNumber(accountNumber: String): AccountEntity?

    @Query("SELECT a FROM AccountEntity a WHERE a.userId = :userId AND a.isActive = TRUE")
    fun findByUserId(@Param("userId") userId: Long): List<AccountListItemProjection>

    @Query("SELECT COUNT(a) FROM AccountEntity a WHERE a.userId = :userId AND a.isActive = TRUE")
    fun getAccountCountByUserId(@Param("userId") userId: Long): Long
}