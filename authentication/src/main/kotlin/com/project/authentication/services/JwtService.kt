package com.project.authentication.services

import com.project.authentication.entities.UserEntity

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtService {
    private val secretKey: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    private val accessTokenExpirationMs: Long = 1000 * 60 * 15
    private val refreshTokenExpirationMs: Long = 1000 * 60 * 60 * 24 * 7

    fun generateTokenPair(user: UserEntity): Pair<String, String> =
        Pair(
            generateAccessToken(user),
            generateRefreshToken(user)
        )

    fun generateAccessToken(user: UserEntity): String =
        generateToken(user, secretKey, accessTokenExpirationMs, "access")

    fun generateRefreshToken(user: UserEntity): String =
        generateToken(user, secretKey, refreshTokenExpirationMs, "refresh")


    private fun generateToken(
        user: UserEntity,
        key: SecretKey,
        expirationMs: Long,
        type: String
    ): String {
        val now = Date()
        val expiry = Date(now.time + expirationMs)
        val roles = user.roles.map { it.name }

        return Jwts.builder()
            .setSubject(user.username)
            .claim("userId", user.id.toString())
            .claim("roles", roles)
            .claim("isActive", user.isActive)
            .claim("type", type)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key)
            .compact()
    }


    fun extractUsername(token: String): String =
        parseToken(token).subject

    fun extractUserId(token: String): UUID =
        UUID.fromString(parseToken(token).get("userId", String::class.java))


    fun extractRole(token: String): String =
        parseToken(token).get("role", String::class.java)

    fun isTokenValid(token: String, username: String): Boolean =
        extractUsername(token) == username

    private fun parseToken(token: String): Claims =
        Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
}