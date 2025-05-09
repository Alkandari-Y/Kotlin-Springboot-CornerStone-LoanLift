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
            c.categoryId,
            c.title,
            c.goalAmount,
            c.status,
            c.submittedAt,
            c.campaignDeadline,
            c.imageUrl
        ) FROM CampaignEntity c
            WHERE c.status NOT IN (
                com.project.campaignlift.entities.CampaignStatus.NEW,
                com.project.campaignlift.entities.CampaignStatus.PENDING,
                com.project.campaignlift.entities.CampaignStatus.REJECTED
            )
    """
    )
    fun listAllCampaigns(): List<CampaignListItemResponse>

    @Query(
        """
        SELECT new com.project.campaignlift.campaigns.dtos.CampaignListItemResponse(
            c.id,
            c.createdBy,
            c.categoryId,
            c.title,
            c.goalAmount,
            c.status,
            c.submittedAt,
            c.campaignDeadline,
            c.imageUrl
        ) FROM CampaignEntity c
            WHERE c.status = :status
    """
    )
    fun findByStatus(@Param("status") status: CampaignStatus): List<CampaignListItemResponse>

    @Query(
        """
        SELECT new com.project.campaignlift.campaigns.dtos.CampaignListItemResponse(
            c.id,
            c.createdBy,
            c.categoryId,
            c.title,
            c.goalAmount,
            c.status,
            c.submittedAt,
            c.campaignDeadline,
            c.imageUrl
        ) FROM CampaignEntity c
            WHERE c.createdBy = :userId
        """
    )
    fun findByCreatedId(@Param("userId") userId: Long): List<CampaignListItemResponse>


}