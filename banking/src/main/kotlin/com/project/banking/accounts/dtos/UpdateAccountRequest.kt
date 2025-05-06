package com.project.banking.accounts.dtos

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

data class UpdateAccountRequest(
    @field:NotBlank()
    @field:Length(min = 1, max = 50)
    val name: String,

    val asPrimary: Boolean?
)
