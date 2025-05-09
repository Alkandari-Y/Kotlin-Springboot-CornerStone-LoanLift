package com.project.campaignlift.repositories

import com.project.banking.entities.TransactionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransferRepository: JpaRepository<TransactionEntity, Long> {

}