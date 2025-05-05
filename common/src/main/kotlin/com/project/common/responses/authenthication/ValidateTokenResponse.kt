package com.project.common.responses.authenthication

import java.util.UUID

data class ValidateTokenResponse (
    val userId: String,
    val isActive: Boolean,
    val roles: List<String>
)