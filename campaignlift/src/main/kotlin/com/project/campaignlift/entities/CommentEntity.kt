package com.project.campaignlift.entities

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "comments")
data class CommentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "created_id", nullable = false)
    val createdBy: Long? = null,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDate? = null,

    @Column(name = "message")
    val message: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    val campaign: CampaignEntity? = null

) {
    constructor() : this(id = null, createdBy = null, createdAt = null, message = null, campaign = null)
}
