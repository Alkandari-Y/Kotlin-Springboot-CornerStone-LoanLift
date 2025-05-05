package com.project.authentication.auth

import com.project.authentication.auth.dtos.JwtResponseDto
import com.project.authentication.auth.dtos.LoginRequest
import com.project.authentication.auth.dtos.RegisterRequest
import com.project.authentication.exceptions.InvalidCredentialsException
import com.project.authentication.exceptions.UserExistsException
import com.project.authentication.exceptions.UserNotFoundException
import com.project.authentication.services.JwtService
import com.project.authentication.services.UserService
import com.project.common.responses.authenthication.ValidateTokenResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import java.security.Principal

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
        val (access, refresh) = jwtService.generateTokenPair(user)
        return ResponseEntity.ok(
            JwtResponseDto(
                access = access,
                refresh = refresh
            )
        )
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<JwtResponseDto> {
        val authToken = UsernamePasswordAuthenticationToken(request.username, request.password)
        val authentication = authenticationManager.authenticate(authToken)

        if (!authentication.isAuthenticated) {
            throw throw InvalidCredentialsException()
        }

        val user = userService.findByUsername(request.username)
            ?: throw InvalidCredentialsException()

        val (access, refresh) = jwtService.generateTokenPair(user)
        return ResponseEntity.ok(
            JwtResponseDto(
                access = access,
                refresh = refresh
            )
        )
    }

    @PostMapping("/validate")
    fun validateToken(principal: Principal
    ): ValidateTokenResponse {
        val user = userService.findByUsername(principal.name)
            ?: throw UserNotFoundException()
        return ValidateTokenResponse(
            userId = user.id.toString(),
            isActive = user.isActive,
            roles = user.roles.map { it.name }
        )
    }
}


