package com.project.campaignlift.repositories

import com.project.campaignlift.comments.dtos.CommentResponseDto
import com.project.campaignlift.entities.CommentEntity
import com.project.campaignlift.entities.projections.CommentProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository: JpaRepository<CommentEntity, Long> {
//
//    @Query("""
//        SELECT new com.project.campaignlift.comments.dtos.CommentResponseDto(
//            c.id,
//            c.campaign.id,
//            c.message,
//            c.createdBy,
//            c.createdAt,
//            null
//        )
//        FROM CommentEntity c
//        WHERE c.campaign.id = :campaignId
//    """)
//    fun getAllByCampaignId(@Param("campaignId") campaignId: Long): List<CommentResponseDto>

    fun findByCampaignId(campaignId: Long): List<CommentProjection>


}


