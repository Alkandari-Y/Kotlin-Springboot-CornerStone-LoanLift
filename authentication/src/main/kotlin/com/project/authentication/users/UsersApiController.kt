package com.project.authentication.users

import com.project.authentication.services.RoleService
import com.project.authentication.services.UserService
import com.project.authentication.users.dtos.RolesAssignmentRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal


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
        @RequestBody newRoles: RolesAssignmentRequest,
        principal: Principal
    ): ResponseEntity<Unit> {
        roleService.assignRolesToUser(userId, newRoles.roles)
        println(principal.name)
        return ResponseEntity(HttpStatus.OK)
    }
}