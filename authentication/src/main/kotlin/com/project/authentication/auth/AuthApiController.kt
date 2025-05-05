package com.project.authentication.auth

import com.project.authentication.auth.dtos.RegisterRequest
import com.project.authentication.services.JwtService
import com.project.authentication.services.UserService
import com.project.common.exceptions.APIException
import com.project.common.exceptions.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthApiController(
    private val authenticationManager: AuthenticationManager,
    private val userService: UserService,
    private val jwtService: JwtService
) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<JwtResponseDto> {
        val user = userService.createUser(request)
        val token = jwtService.generateToken(user)
        return ResponseEntity.ok(JwtResponseDto(token))
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<JwtResponseDto> {
        val authToken = UsernamePasswordAuthenticationToken(request.username, request.password)
        val authentication = authenticationManager.authenticate(authToken)

        if (!authentication.isAuthenticated) {
            throw APIException(
                "Invalid credentials",
                HttpStatus.UNAUTHORIZED,
                ErrorCode.INVALID_CREDENTIALS
            )
        }

        val user = userService.findByUsername(request.username)
            ?: throw APIException("Invalid credentials", HttpStatus.NOT_FOUND, ErrorCode.INVALID_CREDENTIALS)

        val token = jwtService.generateToken(user)
        return ResponseEntity.ok(JwtResponseDto(token))
    }
}

data class JwtResponseDto(
    val token: String
)


data class LoginRequest(
    val username: String,
    val password: String
)
