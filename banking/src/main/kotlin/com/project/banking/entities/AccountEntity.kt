package com.project.banking.entities


import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "accounts")
data class AccountEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "name")
    val name: String,

    @Column(name = "balance")
    val balance: BigDecimal,

    @Column(name = "is_active")
    val isActive: Boolean = true,

    @Column(name = "is_deleted", unique = true)
    val isDeleted: Boolean = false,

    @Column(name = "account_number", unique = true)
    val accountNumber: String = UUID.randomUUID().toString()
        .replace("[A-Za-z]".toRegex(), "")
        .replace("-", ""),

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),


    @OneToOne(mappedBy = "account", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    val owner: AccountOwnershipEntity? = null
    ) {
    constructor() : this(
        id = null,
        name = "",
        balance = BigDecimal.ZERO,
        isActive = true,
        isDeleted = false,
        accountNumber = UUID.randomUUID().toString()
            .replace("[A-Za-z]".toRegex(), "")
            .replace("-", ""))
}