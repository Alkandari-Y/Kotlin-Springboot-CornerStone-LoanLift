package com.project.campaignlift.campaigns.dtos

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class FileUploadRequest(
    @field:NotNull(message = "Campaign ID is required")
    @field:Min(value = 1, message = "Campaign ID must be greater than 0")
    val campaignId: Long,

    val isPublic: Boolean = false
)