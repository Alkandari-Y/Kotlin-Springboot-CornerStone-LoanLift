package com.project.banking.entities


import jakarta.persistence.*
import java.math.BigDecimal
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
    val active: Boolean = true,

    @Column(name = "owner_id")
    val ownerId: Long? = null,

    @Column(name = "owner_type")
    val ownerType: AccountType = AccountType.USER,

    @Column(name = "account_number", unique = true)
    val accountNumber: String = UUID.randomUUID().toString()
        .replace("[A-Za-z]".toRegex(), "")
        .replace("-", ""),

    ) {
    constructor() : this(
        id = null,
        name = "",
        balance = BigDecimal.ZERO,
        active = true,
        ownerId = null,
        accountNumber = UUID.randomUUID().toString()
            .replace("[A-Za-z]".toRegex(), "")
            .replace("-", ""))
}


enum class AccountType {
    USER, CAMPAIGN
}