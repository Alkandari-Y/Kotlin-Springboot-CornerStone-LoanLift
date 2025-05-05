package com.project.authentication.services

import com.project.authentication.auth.dtos.RegisterRequest
import com.project.authentication.auth.dtos.toUserEntity
import com.project.authentication.entities.UserEntity
import com.project.authentication.exceptions.UserExistsException
import com.project.authentication.respositories.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val roleService: RoleService
): UserService {
    override fun createUser(newUserRequest: RegisterRequest): UserEntity {
        val userExists = userRepository.existsByUsernameOrEmailOrCivilId(
            newUserRequest.username,
            newUserRequest.email,
            newUserRequest.civilId
        )

        if (userExists) {
           throw UserExistsException()
        }

        val role = roleService.getDefaultRole()

        return userRepository.save(
            newUserRequest.toUserEntity(
                passwordEncoder.encode(newUserRequest.password),
                setOf(role)
            )
        )
    }

    override fun findByUsername(username: String): UserEntity? {
        return userRepository.findByUsername(username)
    }


}