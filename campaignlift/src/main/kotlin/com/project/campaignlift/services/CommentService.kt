package com.project.campaignlift.services

import com.project.campaignlift.campaigns.dtos.CommentResponseDto
import com.project.campaignlift.entities.CommentEntity
import com.project.common.responses.authenthication.UserInfoDto

interface CommentService {
    fun createComment(campaignId: Long, comment: String, user: UserInfoDto): CommentEntity
    fun getAllCommentsByCampaignId(campaignId: Long): List<CommentResponseDto>
//    fun deleteComment(id: Long)
//    fun getAllComments(): List<CommentResponseDto>

}