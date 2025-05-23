package com.project.campaignlift.services

import com.project.campaignlift.entities.CommentEntity
import com.project.campaignlift.entities.ReplyEntity
import com.project.campaignlift.entities.projections.CommentProjection
import com.project.campaignlift.repositories.CampaignRepository
import com.project.campaignlift.repositories.CommentRepository
import com.project.campaignlift.repositories.ReplyRepository
import com.project.common.exceptions.campaigns.CampaignNotFoundException
import com.project.common.exceptions.comments.CommentDeleteException
import com.project.common.exceptions.comments.CommentNotFoundException
import com.project.common.exceptions.comments.CommentPermissionException
import com.project.common.exceptions.comments.ReplyAlreadyExistsException
import com.project.common.exceptions.kycs.AccountNotVerifiedException
import com.project.common.responses.authenthication.UserInfoDto
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CommentServiceImpl (
    private val campaignRepository: CampaignRepository,
    private val commentRepository: CommentRepository,
    private val replyRepository: ReplyRepository,
): CommentService {
    override fun createComment(campaignId: Long, message: String, user: UserInfoDto): CommentEntity {
        if (user.isActive.not()) {
            throw AccountNotVerifiedException()
        }

        val campaign = campaignRepository.findByIdOrNull(campaignId)
            ?: throw CampaignNotFoundException()

        val comment = CommentEntity(
            campaign = campaign,
            message = message,
            createdBy = user.userId,
            createdAt = LocalDateTime.now(),
        )
        return commentRepository.save(
            comment
        )
    }

    override fun getAllCommentsByCampaignId(campaignId: Long): List<CommentProjection> {
        return commentRepository.findByCampaignId(campaignId)
    }

    override fun getCommentById(commentId: Long): CommentEntity? {
        return commentRepository.findByIdOrNull(commentId)
    }

    override fun deleteComment(commentId: Long, userId: Long) {
        val comment = commentRepository.findByIdOrNull(commentId)
            ?: throw CommentNotFoundException()

        if (replyRepository.existsByCommentId(commentId)) {
            throw CommentDeleteException()
        }

        if (userId != comment.createdBy) {
            throw CommentPermissionException()
        }

        commentRepository.deleteById(commentId)
    }

    override fun createReply(
        commentId: Long,
        message: String,
        userId: Long
    ): ReplyEntity {
        val comment = commentRepository.findByIdOrNull(commentId)
            ?: throw CommentNotFoundException()

        val campaignOwner = comment.campaign?.createdBy == userId

        if (campaignOwner.not()) {
            throw AccountNotVerifiedException()
        }

        if (comment.reply != null) {
            throw ReplyAlreadyExistsException()
        }

        val reply = ReplyEntity(
            comment = comment,
            message = message
        )
        return replyRepository.save(reply)
    }

    override fun deletedCommentAndReply(commentId: Long) {
        commentRepository.deleteById(commentId)
    }
}