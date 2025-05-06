package com.project.common.responses.authenthication

data class ValidateTokenResponse (
    val userId: String,
    val isActive: Boolean,
    val roles: List<String>
)