package com.project.authentication.auth.dtos

import com.project.authentication.entities.RoleEntity
import com.project.authentication.entities.UserEntity

data class RegisterRequest(
    val username: String,
    val email: String,
    val civilId: String,
    val password: String
)

fun RegisterRequest.toUserEntity(hashedPassword: String, roles: Set<RoleEntity>): UserEntity {
    val user = UserEntity(
        username = username,
        email = email,
        civilId = civilId,
        password = hashedPassword,
        isActive = false,
        roles = roles
    )
    return user
}