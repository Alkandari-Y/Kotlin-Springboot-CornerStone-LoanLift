package com.project.authentication.auth

import com.project.authentication.entities.UserEntity
import com.project.authentication.respositories.UserRepository
import com.project.authentication.services.JwtService
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthApiController(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<Any> {
        val user = UserEntity(
            username = request.username,
            email = request.email,
            civilId = request.civilId,
            password = passwordEncoder.encode(request.password),
            isActive = true,
            roles = setOf() // Add default role here
        )
        userRepository.save(user)
        return ResponseEntity.ok("User registered")
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Any> {
        val user = userRepository.findByUsername(request.username)
            ?: return ResponseEntity.status(401).body("User not found")

        if (!passwordEncoder.matches(request.password, user.password)) {
            return ResponseEntity.status(401).body("Invalid credentials")
        }

        val token = jwtService.generateToken(user)
        return ResponseEntity.ok(mapOf("token" to token))
    }
}

data class RegisterRequest(
    val username: String,
    val email: String,
    val civilId: String,
    val password: String
)

data class LoginRequest(
    val username: String,
    val password: String
)
