package com.project.authentication.auth.dtos

data class JwtResponseDto(
    val access: String,
    val refresh: String
)