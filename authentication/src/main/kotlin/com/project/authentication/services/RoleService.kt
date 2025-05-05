package com.project.authentication.services

import com.project.authentication.entities.RoleEntity

interface RoleService {
    fun getDefaultRole(): RoleEntity
    fun createRole(role: RoleEntity): RoleEntity
}