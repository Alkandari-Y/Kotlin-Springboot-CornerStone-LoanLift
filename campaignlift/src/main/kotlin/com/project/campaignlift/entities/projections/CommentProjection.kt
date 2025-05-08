package com.project.campaignlift.entities.projections

import java.time.LocalDateTime

interface CommentProjection {
    fun getId(): Long
    fun getMessage(): String
    fun getCreatedBy(): Long
    fun getCreatedAt(): LocalDateTime
    fun getCampaign(): CampaignIdProjection
    fun getReply(): ReplyProjection?
}

interface CampaignIdProjection {
    fun getId(): Long
}

interface ReplyProjection {
    fun getId(): Long
    fun getMessage(): String
    fun getCreatedAt(): LocalDateTime
}
