package com.project.authentication.entities

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority

@Entity
@Table(name = "roles")
class RoleEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "name", nullable = false, unique = true)
    val name: String = "",

    @ManyToMany(mappedBy = "roles")
    val users: Set<UserEntity> = emptySet()
) : GrantedAuthority {
    override fun getAuthority(): String = name
}

