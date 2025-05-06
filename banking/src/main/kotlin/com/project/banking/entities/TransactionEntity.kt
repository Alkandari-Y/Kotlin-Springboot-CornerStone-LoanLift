package com.project.banking.entities

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "transactions")
data class TransactionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account", nullable = false)
    @JsonBackReference
    val sourceAccount: AccountEntity?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_account", nullable = false)
    @JsonBackReference
    val destinationAccount: AccountEntity?,

    @Column(name = "amount", nullable = false)
    val amount: BigDecimal,

    @CreationTimestamp
    val createdDate: Instant = Instant.now(),
    ) {
    constructor() : this(
        id = null,
        sourceAccount = null,
        destinationAccount = null,
        amount = BigDecimal.ZERO,
    )
}