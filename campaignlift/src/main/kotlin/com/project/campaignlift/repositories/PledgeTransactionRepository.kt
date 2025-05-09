package com.project.campaignlift.repositories

import com.project.campaignlift.entities.PledgeTransactionEntity
import com.project.campaignlift.pledges.dtos.PledgeTransactionWithDetails
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PledgeTransactionRepository: JpaRepository<PledgeTransactionEntity, Long> {
    @Query("""
    SELECT new com.project.campaignlift.pledges.dtos.PledgeTransactionWithDetails(
        pt.id, pt.type, t.id, t.amount, t.type, t.createdAt
    )
    FROM PledgeTransactionEntity pt
    JOIN TransactionEntity t ON pt.transactionId = t.id
    WHERE pt.pledge.id = :pledgeId
""")
    fun findDetailsByPledgeId(@Param("pledgeId") pledgeId: Long): List<PledgeTransactionWithDetails>

}