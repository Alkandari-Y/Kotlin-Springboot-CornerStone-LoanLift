package com.project.authentication.auth.dtos

data class LoginRequest(
    val username: String,
    val password: String
)