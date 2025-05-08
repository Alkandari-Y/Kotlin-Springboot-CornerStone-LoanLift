package com.project.campaignlift.services

import com.project.campaignlift.entities.CommentEntity
import org.springframework.stereotype.Service

@Service
class CommentServiceImpl: CommentService {
    override fun createComment(campaignId: Long, comment: String, userId): CommentEntity {

    }

    override fun getAllComments(): List<CommentEntity> {
        TODO("Not yet implemented")
    }

    override fun getCommentsByCampaign(campaignId: Long): List<CommentEntity> {
        TODO("Not yet implemented")
    }

    override fun deleteComment(id: Long) {
        TODO("Not yet implemented")
    }

}