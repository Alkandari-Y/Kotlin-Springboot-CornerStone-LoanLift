package com.project.campaignlift.entities

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate


@Entity
@Table(name = "pledges")
data class PledgeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "account_id", nullable = false)
    val accountId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    val campaign: CampaignEntity,

    @Column(name = "amount", nullable = false, columnDefinition = "DECIMAL(9,3)")
    val amount: BigDecimal,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDate = LocalDate.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDate = LocalDate.now(),

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    val status: PledgeStatus = PledgeStatus.COMMITTED,

    @Column(name = "withdrawn_at")
    val withdrawnAt: LocalDate? = null,

    @Column(name = "commited_at", nullable = false)
    val commitedAt: LocalDate = LocalDate.now(),

    @OneToMany(mappedBy = "pledge", cascade = [CascadeType.ALL], orphanRemoval = true)
    val transactions: List<PledgeTransactionEntity> = emptyList()
) {
    constructor() : this(
        id = null,
        userId = 0L,
        accountId = 0L,
        campaign = CampaignEntity(),
        amount = BigDecimal.ZERO,
        createdAt = LocalDate.now(),
        updatedAt = LocalDate.now(),
        status = PledgeStatus.COMMITTED,
        withdrawnAt = null,
        commitedAt = LocalDate.now(),
        transactions = emptyList()
    )
}


enum class PledgeStatus {
    COMMITTED,
    WITHDRAWN,
}
