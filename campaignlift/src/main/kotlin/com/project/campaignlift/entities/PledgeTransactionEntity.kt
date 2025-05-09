package com.project.campaignlift.entities

import jakarta.persistence.*

@Entity
@Table(name = "pledge_transactions")
data class PledgeTransactionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "transaction_id", nullable = false)
    val transactionId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pledge_id", nullable = false)
    val pledge: PledgeEntity,

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    val type: PledgeTransactionType
) {
    constructor() : this(
        id = null,
        transactionId = 0L,
        pledge = PledgeEntity(),
        type = PledgeTransactionType.FUNDING
    )
}


enum class PledgeTransactionType {
    FUNDING,     // Money going into pledge
    REFUND,      // Money refunded to user
    REPAYMENT    // Repayment from campaign
}
