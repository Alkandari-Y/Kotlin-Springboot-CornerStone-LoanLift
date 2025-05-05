package com.project.authentication.services

import com.project.authentication.auth.dtos.RegisterRequest
import com.project.authentication.entities.UserEntity

interface UserService {
    fun createUser(newUserRequest: RegisterRequest): UserEntity
    fun findByUsername(username: String): UserEntity?
}