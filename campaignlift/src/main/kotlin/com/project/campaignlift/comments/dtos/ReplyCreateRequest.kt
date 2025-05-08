package com.project.campaignlift.comments.dtos

import jakarta.validation.constraints.Min
import org.jetbrains.annotations.NotNull

data class ReplyCreateRequest(
    @field:NotNull
    @field:Min(1)
    val commentId: Long,
    val message: String,
)