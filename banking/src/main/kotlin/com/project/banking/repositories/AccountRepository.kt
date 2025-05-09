package com.project.banking.repositories

import com.project.banking.entities.AccountEntity
import com.project.banking.repositories.projections.AccountView
import com.project.common.enums.AccountType
import com.project.common.responses.banking.AccountResponse
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository: JpaRepository<AccountEntity, Long> {

    @Query("""
        SELECT a
        FROM AccountEntity a
        WHERE a.ownerId = :ownerId AND a.active = true
    """)
    fun findByOwnerIdActive(@Param("ownerId") ownerId: Long): List<AccountView>


    @Query("""
        SELECT a
        FROM AccountEntity a
        WHERE a.ownerId = :ownerId
    """)
    fun findAllByOwnerId(@Param("ownerId") ownerId: Long): List<AccountEntity>

    @Query("""
        SELECT COUNT(a) FROM AccountEntity a 
        WHERE a.ownerId = :userId 
        AND a.active = true 
        AND a.ownerType = :ownerType
    """)
    fun getAccountCountByUserId(
        @Param("userId") userId: Long,
        @Param("ownerType") ownerType: AccountType = AccountType.USER
    ): Long


    fun findByAccountNumber(accountNumber: String): AccountEntity?
}