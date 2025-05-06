package com.project.banking.repositories

import com.project.banking.entities.AccountOwnershipEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AccountOwnershipRepository: JpaRepository<AccountOwnershipEntity, Long> {
    @Query("SELECT COUNT(a) FROM AccountOwnershipEntity a WHERE a.ownerId = :ownerId")
    fun getAccountCountByUserId(@Param("ownerId") ownerId: Long): Long
}
