package com.project.campaignlift.repositories

import com.project.campaignlift.campaigns.dtos.CampaignListItemResponse
import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CampaignRepositories: JpaRepository<CampaignEntity, Long> {

    @Query("""
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
        WHERE c.status != 'NEW' OR c.status != 'PENDING' OR c.status != 'REJECTED'
    """)
    fun listAllCampaigns(): List<CampaignListItemResponse>

    @Query("""
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
    """)
    fun findByStatus(@Param("status")status: CampaignStatus): List<CampaignListItemResponse>
    fun findByCreatedBy(userId: Long): List<CampaignEntity>

//
//    @Query("""
//    SELECT new com.project.campaignlift.campaigns.dtos.CampaignWithCommentsDto(
//        c.id,
//        c.createdBy,
//        c.categoryId,
//        c.title,
//        c.description,
//        c.goalAmount,
//        c.interestRate,
//        c.repaymentMonths,
//        c.status,
//        c.submittedAt,
//        c.approvedBy,
//        c.campaignDeadline,
//        c.accountId,
//        c.imageUrl
//    )
//    FROM CampaignEntity c
//    WHERE c.id = :campaignId
//""")
//    fun getCampaignDetailsWithoutComments(@Param("campaignId") campaignId: Long): CampaignWithCommentsDto?
}