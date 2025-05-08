package com.project.campaignlift.services

import com.project.campaignlift.entities.CommentEntity
import com.project.campaignlift.entities.projections.CommentProjection
import com.project.campaignlift.repositories.CommentRepository
import com.project.common.exceptions.APIException
import com.project.common.exceptions.ErrorCode
import com.project.common.responses.authenthication.UserInfoDto
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CommentServiceImpl (
    private val campaignService: CampaignService,
    private val commentRepository: CommentRepository,
): CommentService {
    override fun createComment(campaignId: Long, comment: String, user: UserInfoDto): CommentEntity {
        if (user.isActive.not() || user.userId == null) {
            throw APIException(
                message = "complete kyc registration process",
                httpStatus = HttpStatus.BAD_REQUEST,
                code = ErrorCode.INCOMPLETE_USER_REGISTRATION
            )
        }

        val campaign = campaignService.getCampaignById(campaignId)
            ?: throw APIException(
                message = "Campaign with id $campaignId not found",
                httpStatus = HttpStatus.NOT_FOUND,
                code = ErrorCode.CAMPAIGN_NOT_FOUND
            )
        val comment = CommentEntity(
            campaign = campaign,
            message = comment,
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

//
//    override fun deleteComment(id: Long) {
//        TODO("Not yet implemented")
//    }

}