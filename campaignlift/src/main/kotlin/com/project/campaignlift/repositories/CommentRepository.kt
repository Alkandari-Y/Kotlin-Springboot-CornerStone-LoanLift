package com.project.campaignlift.repositories

import com.project.campaignlift.campaigns.dtos.CommentResponseDto
import com.project.campaignlift.entities.CommentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository: JpaRepository<CommentEntity, Long> {

    @Query("""
        SELECT new com.project.campaignlift.campaigns.dtos.CommentResponseDto(
            c.id,
            c.campaign.id,
            c.message,
            c.createdBy,
            c.createdAt
        )
            FROM CommentEntity c 
            WHERE c.campaign = :campaignId
    """
    )
    fun getAllByCampaignId(@Param("campaignId") campaignId: Long): List<CommentResponseDto>
}