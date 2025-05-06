package com.project.common.responses.authenthication

data class UserInfoDto(
    val userId: Long,
    val isActive: Boolean,
    val email: String,
    val username: String
)
