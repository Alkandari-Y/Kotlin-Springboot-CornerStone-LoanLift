package com.project.banking.kycs.dtos

import com.project.banking.entities.KYCEntity
import com.project.common.utils.dateFormatter
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull
import org.springframework.format.annotation.DateTimeFormat
import java.math.BigDecimal
import java.time.LocalDate

data class KYCRequest(
    @field:NotBlank
    val firstName: String,
    @field:NotBlank
    val lastName: String,
    @field:NotBlank
    @field:DateTimeFormat(pattern = "dd-MM-yyyy")
    val dateOfBirth: String,
    @field:NotNull
    @field:DecimalMin(value = "0.00", inclusive = true, message = "Minimum salary cannot be below 0")
    val salary: BigDecimal,
    @field:NotBlank
    val nationality: String,
)

fun KYCRequest.toEntity(userId: Long) = KYCEntity(
    userId = userId,
    firstName = firstName,
    lastName = lastName,
    dateOfBirth = LocalDate.parse(dateOfBirth, dateFormatter),
    salary = salary,
    nationality = nationality
)
