package com.project.authentication.users.dtos

import jakarta.validation.constraints.Size


data class RolesAssignmentRequest (
    @field:Size(min = 1, max = 5)
    val roles: List<String>
)