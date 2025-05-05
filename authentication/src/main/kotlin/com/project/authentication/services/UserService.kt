package com.project.authentication.services

import com.project.authentication.auth.dtos.RegisterCreateRequest
import com.project.authentication.entities.UserEntity

interface UserService {
    fun createUser(user: RegisterCreateRequest): UserEntity
    fun findUserById(userId: Long): UserEntity?
    fun findUserByUsername(username: String): UserEntity?
    fun setActiveUser(userId: Long)
}