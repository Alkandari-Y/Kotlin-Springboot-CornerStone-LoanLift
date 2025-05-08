package com.project.campaignlift.comments.dtos

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

data class CommentCreateRequest(
    @field:NotBlank
    @field:Length(min = 3, max = 50)
    val message: String,
)