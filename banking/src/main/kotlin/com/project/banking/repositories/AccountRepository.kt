package com.project.banking.repositories

import com.project.banking.accounts.dtos.AccountResponse
import com.project.banking.entities.AccountEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository: JpaRepository<AccountEntity, Long> {

    @Query("""
    SELECT new com.project.banking.accounts.dtos.AccountResponse(
        a.id,
        a.accountNumber,
        a.name,
        a.balance,
        a.active,
        a.ownerId
    )
    FROM AccountEntity a
    WHERE a.ownerId = :ownerId AND a.active = true
""")
    fun findAllActiveByOwnerId(@Param("ownerId") ownerId: Long): List<AccountResponse>

    @Query("""
    SELECT a
    FROM AccountEntity a
    WHERE a.ownerId = :ownerId
""")
    fun findAllByOwnerId(@Param("ownerId") ownerId: Long): List<AccountEntity>

    @Query("SELECT COUNT(a) FROM AccountEntity a WHERE a.ownerId = :userId AND a.active = TRUE")
    fun getAccountCountByUserId(@Param("userId") userId: Long): Long

    fun findByAccountNumber(accountNumber: String): AccountEntity?

}