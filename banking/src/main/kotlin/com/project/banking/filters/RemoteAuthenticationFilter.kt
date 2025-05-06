package com.project.banking.filters

import com.project.banking.providers.JwtAuthProvider
import com.project.common.responses.authenthication.toUserInfoDto
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class RemoteAuthenticationFilter(
    private val jwtAuthProvider: JwtAuthProvider
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val bearerToken: String? = request.getHeader("Authorization")
        println("Bearer token: $bearerToken")
        if (bearerToken.isNullOrBlank() || !bearerToken.startsWith("Bearer ")) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Authorization header is missing or invalid")
            return
        }

        try {
            val token = bearerToken.substring(7)
            val result = jwtAuthProvider.authenticateToken(token)
            val userInfo = result.toUserInfoDto()


            request.setAttribute("authUser", userInfo)

            val authorities = result.roles.map { SimpleGrantedAuthority(it) }
            logger.info("User roles: ${result.roles}")

            val authentication = UsernamePasswordAuthenticationToken(
                userInfo,
                null,
                authorities
            )
            SecurityContextHolder.getContext().authentication = authentication

            filterChain.doFilter(request, response)

        } catch (ex: Exception) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "Invalid token")
        }
    }
}