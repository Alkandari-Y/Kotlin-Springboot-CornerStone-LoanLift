package com.project.campaignlift.comments.dtos

import java.time.LocalDateTime

data class ReplyDto(
    val id: Long,
    val message: String,
    val createdAt: LocalDateTime
)
