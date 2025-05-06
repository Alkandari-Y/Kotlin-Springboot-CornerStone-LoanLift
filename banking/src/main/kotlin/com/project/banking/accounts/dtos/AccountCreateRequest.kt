package com.project.banking.accounts.dtos

import com.project.banking.entities.AccountEntity
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.math.RoundingMode

data class AccountCreateRequest(
    @field:NotNull
    @field:DecimalMin(
        value = "100.00",
        inclusive = true,
        message = "Initial balance must be at least 100.00"
    )
    val initialBalance: BigDecimal,

    @field:NotBlank(message = "Account name cannot be blank")
    val name: String
)

fun AccountCreateRequest.toEntity(): AccountEntity {
    return AccountEntity(
        name = name,
        balance = initialBalance.setScale(3, RoundingMode.HALF_UP),
        isActive = true,
        isDeleted = false,
    )
}
