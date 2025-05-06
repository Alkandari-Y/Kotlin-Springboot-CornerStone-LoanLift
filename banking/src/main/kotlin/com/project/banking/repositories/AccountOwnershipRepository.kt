package com.project.banking.repositories

import com.project.banking.entities.AccountOwnerType
import com.project.banking.entities.AccountOwnershipEntity
import com.project.banking.entities.projections.AccountListItemProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AccountOwnershipRepository : JpaRepository<AccountOwnershipEntity, Long> {
    @Query("""
    SELECT aoe FROM AccountOwnershipEntity aoe
    JOIN FETCH aoe.account
    WHERE aoe.account.accountNumber = :accountNumber
""")
    fun findByAccountNumber(@Param("accountNumber") accountNumber: String): AccountOwnershipEntity?


    @Query("SELECT COUNT(a) FROM AccountOwnershipEntity a WHERE a.ownerId = :ownerId")
    fun getAccountCountByUserId(@Param("ownerId") ownerId: Long): Long

    @Query("""
        SELECT a.account FROM AccountOwnershipEntity a
        WHERE a.ownerId = :userId AND a.ownerType = :ownerType
    """)
    fun findAllByOwner(
        @Param("userId") userId: Long,
        @Param("ownerType") ownerType: AccountOwnerType = AccountOwnerType.USER
    ): List<AccountListItemProjection>

    fun findAllByOwnerIdAndOwnerType(ownerId: Long, ownerType: AccountOwnerType): List<AccountOwnershipEntity>

}
