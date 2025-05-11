package com.project.campaignlift.repositories

import com.project.banking.entities.AccountEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository: JpaRepository<AccountEntity, Long> {
    fun findAllByIdIn(ids: Set<Long>): List<AccountEntity>

}