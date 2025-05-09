package com.project.campaignlift.repositories

import com.project.campaignlift.entities.CommentEntity
import com.project.campaignlift.entities.projections.CommentProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository: JpaRepository<CommentEntity, Long> {
    fun findByCampaignId(campaignId: Long): List<CommentProjection>


}


