package com.project.authentication.services

import com.project.authentication.entities.toUserDetails
import com.project.authentication.exceptions.UserNotFoundException
import com.project.authentication.respositories.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class AuthUserDetailsService(
    private val usersRepository: UserRepository,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = usersRepository.findByUsername(username)
            ?: throw UserNotFoundException("User not found")
        return user.toUserDetails()
    }
}
