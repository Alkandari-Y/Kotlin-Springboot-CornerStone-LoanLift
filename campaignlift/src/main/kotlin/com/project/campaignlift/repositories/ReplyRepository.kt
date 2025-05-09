package com.project.campaignlift.repositories

import com.project.campaignlift.entities.ReplyEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReplyRepository: JpaRepository<ReplyEntity, Long> {
    fun existsByCommentId(commentId: Long): Boolean
}