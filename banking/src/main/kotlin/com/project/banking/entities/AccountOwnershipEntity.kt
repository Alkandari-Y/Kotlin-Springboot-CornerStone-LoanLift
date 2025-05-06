package com.project.banking.entities

import jakarta.persistence.*

@Entity
@Table(name = "account_ownerships")
data class AccountOwnershipEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "owner_id")
    val ownerId: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type")
    val ownerType: AccountOwnerType = AccountOwnerType.USER,

    @Column(name = "is_primary")
    val isPrimary: Boolean = false,

    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", unique = true)
    val account: AccountEntity? = null,
) {
    constructor() : this(
        id = null,
        ownerId = null,
        ownerType = AccountOwnerType.USER,
        account = null,
        isPrimary = false,
    )
}

enum class AccountOwnerType {
    USER, CAMPAIGN
}