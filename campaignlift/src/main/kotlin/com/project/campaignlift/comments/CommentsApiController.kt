package com.project.campaignlift.comments

import com.project.campaignlift.comments.dtos.CommentResponseDto
import com.project.campaignlift.comments.dtos.toResponseDto
import com.project.campaignlift.comments.dtos.CommentCreateRequest
import com.project.campaignlift.comments.dtos.ReplyCreateRequest
import com.project.campaignlift.comments.dtos.ReplyDto
import com.project.campaignlift.services.CommentService
import com.project.common.responses.authenthication.UserInfoDto
import com.project.common.security.RemoteUserPrincipal
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/comments")
class CommentsApiController(
    private val commentService: CommentService,
    ) {

    @GetMapping("/campaign/{campaignId}")
    fun allAllCampaignComments(
        @PathVariable("campaignId") campaignId: Long,
    ) = commentService.getAllCommentsByCampaignId(campaignId)


    @PostMapping("/campaign/{campaignId}")
    fun addComment(
        @PathVariable("campaignId") campaignId: Long,
        @Valid @RequestBody commentRequest: CommentCreateRequest,
        @RequestAttribute("authUser") authUser: UserInfoDto,
    ): ResponseEntity<CommentResponseDto> {
        val comment = commentService.createComment(
            campaignId = campaignId,
            message = commentRequest.message,
            user = authUser
        ).toResponseDto()
        return ResponseEntity(comment, HttpStatus.CREATED)
    }

    @DeleteMapping("/edit/{commentId}")
    fun deleteComment(
        @PathVariable("commentId") commentId: Long,
        @RequestAttribute("authUser") authUser: UserInfoDto,
        @AuthenticationPrincipal user: RemoteUserPrincipal
    ) {
        val isAdmin = user.authorities.any { it.authority == "ROLE_ADMIN" }
        if (isAdmin) {
            commentService.deletedCommentAndReply(commentId)
        } else {
            commentService.deleteComment(commentId, authUser.userId)
        }
    }

    @PostMapping("/reply")
    fun replyToComment(
        @Valid @RequestBody replyCreate: ReplyCreateRequest,
        @RequestAttribute("authUser") authUser: UserInfoDto,
        ): ResponseEntity<ReplyDto> {
        val reply = commentService.createReply(
            commentId = replyCreate.commentId,
            message = replyCreate.message,
            userId = authUser.userId,
        )
        return ResponseEntity(reply.toResponseDto(), HttpStatus.CREATED)
    }

}