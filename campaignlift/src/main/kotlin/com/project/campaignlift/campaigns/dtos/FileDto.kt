package com.project.campaignlift.campaigns.dtos

import com.project.campaignlift.entities.FileEntity

data class FileDto(
    val id: Long,
    val mediaType: String,
    val downloadUrl: String
)

fun FileEntity.toDto(baseApiUrl: String = "http://localhost:8083"): FileDto =
    FileDto(
        id = this.id!!,
        mediaType = this.mediaType,
        downloadUrl = "$baseApiUrl/api/v1/campaigns/manage/files/${this.id}/download"
    )
