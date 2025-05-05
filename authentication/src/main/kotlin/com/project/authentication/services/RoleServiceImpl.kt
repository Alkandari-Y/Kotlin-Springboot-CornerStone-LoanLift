package com.project.authentication.services

import com.project.authentication.entities.RoleEntity
import com.project.authentication.respositories.RoleRepository
import org.springframework.stereotype.Service

@Service
class RoleServiceImpl(
    private val roleRepository: RoleRepository,
): RoleService {
    override fun getDefaultRole(): RoleEntity {
        // TODO("ADD AS CONFIGURATION LATER")
        val defaultRoleName = "ROLE_USER"
        val defaultRole = roleRepository.findByName(defaultRoleName)
            ?: roleRepository.save(RoleEntity(null, defaultRoleName))

        return defaultRole
    }

    override fun createRole(role: RoleEntity): RoleEntity {
        return roleRepository.save(role)
    }
}