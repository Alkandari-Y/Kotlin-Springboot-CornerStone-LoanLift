package com.project.banking.kycs.dtos

import com.fasterxml.jackson.annotation.JsonFormat
import com.project.banking.entities.KYCEntity
import java.math.BigDecimal
import java.time.LocalDate

data class KYCResponse(
    val id: Long,
    val userId: Long,
    val firstName: String,
    val lastName: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    var dateOfBirth: LocalDate? = null,
    val salary: BigDecimal,
    val nationality: String
)


fun KYCEntity.toResponse() = KYCResponse(
    id = id!!,
    userId = userId!!,
    firstName = firstName,
    lastName = lastName,
    dateOfBirth = dateOfBirth,
    salary = salary,
    nationality = nationality
)