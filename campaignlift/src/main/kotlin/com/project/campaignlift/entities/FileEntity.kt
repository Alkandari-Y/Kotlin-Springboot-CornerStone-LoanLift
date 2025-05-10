package com.project.campaignlift.entities

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "files")
data class FileEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    val campaign: CampaignEntity? = null,

    @Column(name = "media_type", nullable = false)
    val mediaType: String,

    @Column(name = "url", nullable = false)
    val url: String,

    @Column(name = "bucket", nullable = false)
    val bucket: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDate = LocalDate.now(),

    @Column(name = "is_public", nullable = false)
    val isPublic: Boolean = true,

    @Column(name = "verified_by")
    val verifiedBy: Long? = null
) {
    constructor() : this(
        id = null,
        campaign = null,
        mediaType = "",
        url = "",
        bucket = "",
        createdAt = LocalDate.now(),
        isPublic = true,
        verifiedBy = null
    )
}
