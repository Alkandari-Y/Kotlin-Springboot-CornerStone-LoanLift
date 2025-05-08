package com.project.campaignlift.entities

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "campaigns")
data class CampaignEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "created_by", nullable = false)
    val createdBy: Long?,

    @Column(name = "category_id", nullable = false)
    val categoryId: Long?,

    @Column(name = "title", nullable = false)
    val title: String,

    @Column(name = "description")
    val description: String? = null,

    @Column(name = "goal_amount", columnDefinition = "DECIMAL(9, 3) DEFAULT 0.000")
    val goalAmount: BigDecimal? = null,

    @Column(name = "interest_rate", nullable = false, columnDefinition = "DECIMAL(5, 3) DEFAULT 0.000")
    val interestRate: BigDecimal,

    @Column(name = "repayment_months", nullable = false)
    val repaymentMonths: Int,

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    val status: CampaignStatus = CampaignStatus.NEW,

    @Column(name = "submitted_at", updatable = false)
    val submittedAt: LocalDate? = null,

    @Column(name = "approved_by", nullable = true)
    val approvedBy: Long? = null,

    @Column(name = "deadline")
    val campaignDeadline: LocalDate? = null,

    @Column(name = "account_id")
    val accountId: Long? = null,

    @Column(name = "image_url")
    val imageUrl: String? = null,


    @OneToMany(mappedBy = "campaign", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    val comments: List<CommentEntity>? = null,

    @Transient
    var amountRaised: BigDecimal = BigDecimal.ZERO,

    ) {
    constructor() : this(
        id = null,
        createdBy = 0L,
        categoryId = 0L,
        title = "",
        description = null,
        goalAmount = null,
        interestRate = BigDecimal.ZERO,
        repaymentMonths = 0,
        status = CampaignStatus.NEW,
        submittedAt = null,
        approvedBy = 0L,
        campaignDeadline = null,
        accountId = null,
        imageUrl = null,
        amountRaised = BigDecimal.ZERO,
    )
}


enum class CampaignStatus {
    NEW,                // Status of a new Campaign, users can make edits
    PENDING,            // Status of a campaign under admin review
    REJECTED,           // Isn't approved for launch
    ACTIVE,             // Available to users for funding
    FUNDED,             // Reached or surpassed its goal and deadline
    FAILED,             // Didnt reach its goalAmount by deadline
    COMPLETED,          // Finished repayment of funded goal amount plus interest
    DEFAULTED           // Didn't pay back all its funded goal amount plus interest within repayment months
}