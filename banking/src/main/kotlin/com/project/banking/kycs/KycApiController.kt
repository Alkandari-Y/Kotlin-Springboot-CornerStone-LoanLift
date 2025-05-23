package com.project.banking.kycs

import com.project.banking.kycs.dtos.KYCRequest
import com.project.banking.kycs.dtos.KYCResponse
import com.project.banking.kycs.dtos.toResponse
import com.project.banking.services.KYCService
import com.project.common.exceptions.kycs.KycNotFoundException
import com.project.common.responses.authenthication.UserInfoDto
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/kyc")
class KycApiController(
    private val kycService: KYCService,
) {
    @PostMapping
    fun createOrUpdateKYC(
        @Valid @RequestBody kycRequest: KYCRequest,
        @RequestAttribute("authUser") authUser: UserInfoDto,
    ): ResponseEntity<KYCResponse> {
        val kyc = kycService.createKYCOrUpdate(
            kycRequest = kycRequest,
            user = authUser,
        ).toResponse()

        return ResponseEntity(
            kyc,
            HttpStatus.CREATED
        )
    }

    @GetMapping
    fun getKYCBy(
        @RequestAttribute("authUser") authUser: UserInfoDto,
    ): ResponseEntity<KYCResponse> {

        val kyc = kycService.findKYCByUserId(authUser.userId)
            ?: throw KycNotFoundException(authUser.userId)

        return ResponseEntity(
            kyc.toResponse(),
            HttpStatus.OK
        )
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(path = ["/client/{userId}"])
    fun getKYCByUserId(
        @PathVariable("userId") userId: Long
    ): ResponseEntity<KYCResponse> {

        val kyc = kycService.findKYCByUserId(userId)
            ?: throw KycNotFoundException(userId)

        return ResponseEntity(
            kyc.toResponse(),
            HttpStatus.OK
        )
    }
}


