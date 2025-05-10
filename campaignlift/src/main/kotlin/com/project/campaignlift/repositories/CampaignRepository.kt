package com.project.campaignlift.repositories

import com.project.campaignlift.campaigns.dtos.CampaignListItemResponse
import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CampaignRepository : JpaRepository<CampaignEntity, Long> {

    @Query(
        """
    SELECT new com.project.campaignlift.campaigns.dtos.CampaignListItemResponse(
        c.id,
        c.createdBy,
        cat.id,
        c.title,
        COALESCE(c.goalAmount, 0),
        c.status,
        c.campaignDeadline,
        c.imageUrl,
        COALESCE(SUM(p.amount), 0),
        cat.name
    )
    FROM CampaignEntity c
    JOIN c.category cat
    LEFT JOIN c.pledges p ON p.status = com.project.campaignlift.entities.PledgeStatus.COMMITTED
        WHERE c.status = :status
    GROUP BY c.id, c.createdBy, cat.id, c.title, c.goalAmount, c.status, c.campaignDeadline, c.imageUrl, cat.name
    """
    )
    fun listAllCampaignsByStatus(@Param("status") status: CampaignStatus): List<CampaignListItemResponse>

    @Query(
        """
        SELECT new com.project.campaignlift.campaigns.dtos.CampaignListItemResponse(
            c.id,
            c.createdBy,
            cat.id,
            c.title,
            c.goalAmount,
            c.status,
            c.campaignDeadline,
            c.imageUrl,
            COALESCE(SUM(p.amount), 0),
            cat.name
        )
        FROM CampaignEntity c
        JOIN c.category cat
        LEFT JOIN c.pledges p ON p.status = com.project.campaignlift.entities.PledgeStatus.COMMITTED
                 WHERE c.status NOT IN (
                    com.project.campaignlift.entities.CampaignStatus.NEW,
                    com.project.campaignlift.entities.CampaignStatus.PENDING,
                    com.project.campaignlift.entities.CampaignStatus.REJECTED
                )
        GROUP BY c.id, c.createdBy, cat.id, c.title, c.goalAmount, c.status, c.campaignDeadline, c.imageUrl, cat.name
    """
    )
    fun listAllApprovedCampaigns(): List<CampaignListItemResponse>

    @Query(
        """
        SELECT new com.project.campaignlift.campaigns.dtos.CampaignListItemResponse(
            c.id,
            c.createdBy,
            cat.id,
            c.title,
            c.goalAmount,
            c.status,
            c.campaignDeadline,
            c.imageUrl,
            COALESCE(SUM(p.amount), 0),
            cat.name
        )
        FROM CampaignEntity c
        JOIN c.category cat
        LEFT JOIN c.pledges p ON p.status = com.project.campaignlift.entities.PledgeStatus.COMMITTED
                 WHERE c.createdBy = :userId
        GROUP BY c.id, c.createdBy, cat.id, c.title, c.goalAmount, c.status, c.campaignDeadline, c.imageUrl, cat.name
    """
    )
    fun listAllCampaignsByUserId(@Param("userId") userId: Long): List<CampaignListItemResponse>


    @Query(
        """
        SELECT new com.project.campaignlift.campaigns.dtos.CampaignListItemResponse(
           c.id,
            c.createdBy,
            cat.id,
            c.title,
            c.goalAmount,
            c.status,
            c.campaignDeadline,
            c.imageUrl,
            COALESCE(SUM(p.amount), 0),
            cat.name
        ) FROM CampaignEntity c
        JOIN c.category cat
        LEFT JOIN c.pledges p ON p.status = com.project.campaignlift.entities.PledgeStatus.COMMITTED
            WHERE c.createdBy = :userId
        GROUP BY c.id, c.createdBy, cat.id, c.title, c.goalAmount, c.status, c.campaignDeadline, c.imageUrl, cat.name
        """
    )
    fun findByCreatedId(@Param("userId") userId: Long): List<CampaignListItemResponse>

    @Query("""
        SELECT c FROM CampaignEntity c
        WHERE c.status = com.project.campaignlift.entities.CampaignStatus.FUNDED
    """)
    fun findAllFundedCampaigns(): List<CampaignEntity>
}