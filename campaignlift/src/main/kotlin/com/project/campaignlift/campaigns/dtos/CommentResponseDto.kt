package com.project.campaignlift.campaigns.dtos

import com.project.campaignlift.entities.CommentEntity
import java.time.LocalDateTime

data class CommentResponseDto(
    val id: Long,
    val campaignId: Long,
    val message: String,
    val createdBy: Long,
    val createdAt: LocalDateTime,
)

fun CommentEntity.toResponseDto() = CommentResponseDto(
    id = id!!,
    campaignId = campaign?.id!!,
    message = message!!,
    createdBy = createdBy!!,
    createdAt = createdAt!!,
)