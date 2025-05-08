package com.project.campaignlift.services

import com.project.campaignlift.entities.CommentEntity

interface CommentService {
    fun createComment(campaignId: Long, comment: String): CommentEntity
    fun getAllComments(): List<CommentEntity>
    fun getCommentsByCampaign(campaignId: Long): List<CommentEntity>
    fun deleteComment(id: Long)
}