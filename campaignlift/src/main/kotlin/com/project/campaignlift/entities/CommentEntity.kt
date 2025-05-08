package com.project.campaignlift.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "comments")
data class CommentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "created_by", nullable = false)
    val createdBy: Long? = null,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime? = LocalDateTime.now(),

    @Column(name = "message")
    val message: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    val campaign: CampaignEntity? = null,

    @OneToOne(mappedBy = "comment", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val reply: ReplyEntity? = null,

    ) {
    constructor() : this(
        id = null,
        createdBy = null,
        createdAt = null,
        message = null,
        campaign = null,
        reply = null
    )
}
