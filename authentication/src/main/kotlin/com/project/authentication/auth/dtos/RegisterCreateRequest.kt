package com.project.authentication.auth.dtos

import com.project.authentication.entities.RoleEntity
import com.project.authentication.entities.UserEntity
import jakarta.validation.constraints.*
import org.hibernate.validator.constraints.Length

data class RegisterCreateRequest(
    @field:NotBlank(message = "Username is required")
    @field:Length(min = 3, message = "Username is too short")
    val username: String,
    @field:NotBlank(message = "Password is required")
    @field:Length(min = 6, message = "Password is too short")
    @field:Pattern(regexp = """(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).*""", message = "Password is too simple")
    val password: String,

    @field:NotBlank(message = "Username is required")
    @field:Email(message = "Email is not valid")
    val email: String,

    @field:NotBlank(message = "Civil Id is required")
    val civilId: String,
)


fun RegisterCreateRequest.toEntity(
    hashedPassword: String,
    roles: Set<RoleEntity>
) = UserEntity(
    username = this.username,
    password = hashedPassword,
    civilId = this.civilId,
    email = this.email,
    isActive = false,
    roles = roles.toMutableSet()
)