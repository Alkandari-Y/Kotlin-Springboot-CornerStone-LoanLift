package com.project.campaignlift.repositories

import com.project.campaignlift.campaigns.dtos.CampaignTransactionViewDto
import com.project.campaignlift.campaigns.dtos.MonthlyRepaymentSummaryProjection
import com.project.campaignlift.entities.PledgeTransactionEntity
import com.project.campaignlift.entities.PledgeTransactionType
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

    @Query("""
    SELECT new com.project.campaignlift.campaigns.dtos.CampaignTransactionViewDto(
        t.id,
        t.type,
        pt.type,
        t.amount,
        t.createdAt
    )
    FROM PledgeTransactionEntity pt
    JOIN pt.pledge p
    JOIN p.campaign c
    JOIN TransactionEntity t ON pt.transactionId = t.id
    WHERE c.id = :campaignId
      AND pt.type IN :types
    ORDER BY t.createdAt DESC
""")
    fun findFundingAndRefundTransactions(
        @Param("campaignId") campaignId: Long,
        @Param("types") types: List<PledgeTransactionType> = listOf(
            PledgeTransactionType.FUNDING,
            PledgeTransactionType.REFUND
        )
    ): List<CampaignTransactionViewDto>


    @Query(
        value = """
        SELECT 
            DATE_TRUNC('month', t.created_at) AS month,
            SUM(t.amount + t.amount * 0.002) AS total_paid_with_bank_fee
        FROM 
            pledge_transactions pt
        JOIN 
            transactions t ON pt.transaction_id = t.id
        JOIN 
            pledges p ON pt.pledge_id = p.id
        WHERE 
            p.campaign_id = :campaignId
            AND pt.type = 2
        GROUP BY 
            month
        ORDER BY 
            month DESC
    """,
        nativeQuery = true
    )
    fun findMonthlyRepaymentSummaries(
        @Param("campaignId") campaignId: Long
    ): List<MonthlyRepaymentSummaryProjection>

}