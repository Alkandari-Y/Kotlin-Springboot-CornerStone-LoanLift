package com.project.campaignlift.services

import com.project.campaignlift.entities.CommentEntity
import com.project.campaignlift.entities.ReplyEntity
import com.project.campaignlift.entities.projections.CommentProjection
import com.project.common.responses.authenthication.UserInfoDto

interface CommentService {
    fun createComment(campaignId: Long, comment: String, user: UserInfoDto): CommentEntity
    fun getAllCommentsByCampaignId(campaignId: Long): List<CommentProjection>
    fun getCommentById(commentId: Long): CommentEntity?
    fun deleteComment(commentId: Long, userId: Long)
    fun createReply(commentId: Long, message: String, userId: Long): ReplyEntity
}