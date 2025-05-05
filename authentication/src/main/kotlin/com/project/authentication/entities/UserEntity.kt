package com.project.authentication.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    val id: Long? = null,

    @Column(name="username", nullable = false, unique = true)
    val username: String = "",

    @Column(name="civil_id", nullable = false, unique = true)
    val civilId: String = "",

    @Column(name="email", nullable = false, unique = true)
    val email: String = "",

    @Column(name="password")
    val password: String = "",

    @Column(name="is_active", nullable = false)
    val isActive: Boolean = false,

    @CreationTimestamp
    @Column(name="created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now(),

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.MERGE])
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    val roles: Set<RoleEntity> = emptySet(),

) {
    constructor() : this(
        id = null,
        username = "",
        civilId = "",
        email = "",
        password = "",
        isActive = false,
        createdAt = Instant.now(),
        updatedAt = Instant.now(),
        roles = emptySet(),
    )
}

fun UserEntity.toUserDetails(): UserDetails {
    val authorities: Collection<GrantedAuthority> = this.roles.map {
        SimpleGrantedAuthority(it.name)
    }

    return object : UserDetails {
        override fun getAuthorities(): Collection<GrantedAuthority> = authorities
        override fun getUsername(): String = this@toUserDetails.username
        override fun getPassword(): String = this@toUserDetails.password
        override fun isAccountNonExpired(): Boolean = true
        override fun isAccountNonLocked(): Boolean = true
        override fun isCredentialsNonExpired(): Boolean = true
        override fun isEnabled(): Boolean = this@toUserDetails.isActive
    }
}