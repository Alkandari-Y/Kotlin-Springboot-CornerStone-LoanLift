package com.project.campaignlift.comments.dtos

import com.project.campaignlift.entities.ReplyEntity
import java.time.LocalDateTime

data class ReplyDto(
    val id: Long,
    val message: String,
    val createdAt: LocalDateTime
)

fun ReplyEntity.toResponseDto() = ReplyDto(
    id = id!!,
    message = message!!,
    createdAt = createdAt!!
)