package com.project.authentication.services

import com.project.authentication.entities.RoleEntity
import com.project.common.exceptions.auth.UserNotFoundException
import com.project.authentication.repositories.RoleRepository
import com.project.authentication.repositories.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class RoleServiceImpl(
    private val roleRepository: RoleRepository,
    private val userRepository: UserRepository
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

    override fun assignRolesToUser(userId: Long, roles: Collection<String>) {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException()

        val fetchedRoles = roleRepository.findAllByNameIn(roles)

        val updatedRoles = user.roles.toMutableSet()
        updatedRoles.addAll(fetchedRoles) // Add new roles
        userRepository.save(user.copy(roles = updatedRoles.toSet()))

    }
}