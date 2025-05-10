package com.project.authentication.users.dtos

import jakarta.validation.constraints.Size

data class RolesAssignmentRequest (
    @field:Size(min = 1, max = 5, message = "At least one role must be provided")
    val roles: List<String> = emptyList()
)