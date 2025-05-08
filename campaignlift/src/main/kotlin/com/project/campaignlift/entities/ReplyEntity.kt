package com.project.campaignlift.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "replies")
data class ReplyEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "message")
    val message: String? = null,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    val comment: CommentEntity? = null

) {
    constructor() : this(
        id = null,
        message = null,
        createdAt = null,
        comment = null
    )
}
