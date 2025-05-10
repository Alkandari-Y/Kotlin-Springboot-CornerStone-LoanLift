package com.project.authentication.users

import com.project.authentication.entities.RoleEntity
import com.project.authentication.services.RoleService
import com.project.authentication.services.UserService
import com.project.authentication.users.dtos.RoleCreateRequest
import com.project.authentication.users.dtos.RolesAssignmentRequest
import com.project.authentication.users.dtos.toEntity
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/users")
class UsersApiController(
    private val userService: UserService,
    private val roleService: RoleService
) {

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add-role/{userId}")
    fun addRole(
        @PathVariable("userId") userId: Long,
        @Valid @RequestBody newRoles: RolesAssignmentRequest,
    ): ResponseEntity<Unit> {
        roleService.assignRolesToUser(userId, newRoles.roles)
        return ResponseEntity(HttpStatus.OK)
    }

    // TODO: SWITCH THIS TO API KEY AUTH
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/set-active/{userId}")
    fun setActiveUser(
        @PathVariable("userId") userId: Long
    ): ResponseEntity<Boolean> {
        val result = userService.setActiveUser(userId)
        return ResponseEntity(
            result,
            HttpStatus.OK
        )
    }

    @PreAuthorize("hasRole('ROLE_DEVELOPER')")
    @PostMapping("/roles")
    fun createNewRoles(
        @Valid @RequestBody newRole: RoleCreateRequest,
    ): ResponseEntity<RoleEntity> {
        return ResponseEntity(
            roleService.createRole(
                newRole.toEntity()
            ),
            HttpStatus.CREATED)
    }

    @PreAuthorize("hasRole('ROLE_DEVELOPER')")
    @PostMapping("/remove-role/{userId}")
    fun removeRole(
        @PathVariable("userId") userId: Long,
        @Valid @RequestBody roleRequest: RolesAssignmentRequest,
    ): ResponseEntity<Unit> {
        roleRequest.roles.forEach { roleName ->
            roleService.removeRoleFromUser(userId, roleName)
        }
        return ResponseEntity(HttpStatus.OK)
    }
}

