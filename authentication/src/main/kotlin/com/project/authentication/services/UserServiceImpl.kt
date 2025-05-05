package com.project.authentication.services

import com.project.authentication.auth.dtos.RegisterCreateRequest
import com.project.authentication.auth.dtos.toEntity
import com.project.authentication.entities.UserEntity
import com.project.authentication.exceptions.UserExistsException
import com.project.authentication.repositories.UserRepository
import com.project.common.exceptions.ErrorCode
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val roleService: RoleService,
    private val passwordEncoder: PasswordEncoder
): UserService {
    override fun createUser(user: RegisterCreateRequest): UserEntity {
        val usernameExists = userRepository.existsByUsernameOrCivilIdOrEmail(user.username, user.civilId, user.email)

        if (usernameExists) {
            throw UserExistsException("User already exists", code = ErrorCode.USER_ALREADY_EXISTS)
        }
        val defaultRole = roleService.getDefaultRole()

        return userRepository.save(user.toEntity(
            hashedPassword = passwordEncoder.encode(user.password),
            roles = setOf(defaultRole)
        ))
    }

    override fun findUserById(userId: Long): UserEntity? {
        return userRepository.findByIdOrNull(userId)
    }

    override fun findUserByUsername(username: String): UserEntity? {
        return userRepository.findByUsername(username)
    }
}