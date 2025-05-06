package com.project.banking.kycs

import com.project.banking.kycs.dtos.KYCRequest
import com.project.banking.kycs.dtos.KYCResponse
import com.project.banking.kycs.dtos.toResponse
import com.project.banking.services.KYCService
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
    @PostMapping(path = ["/kyc"])
    fun createOrUpdateKYC(
        @Valid @RequestBody kycRequest: KYCRequest,
        @RequestAttribute("authUserId") userId: Long,
    ): ResponseEntity<KYCResponse> {

        val kyc = kycService.createKYCOrUpdate(
            kycRequest = kycRequest,
            userId = userId,
        )
        return ResponseEntity(
            kyc.toResponse(),
            HttpStatus.CREATED
        )
    }

    @GetMapping(path = ["/kyc"])
    fun getKYCBy(
        @RequestAttribute("authUserId") userId: Long,
    ): ResponseEntity<KYCResponse> {
        val kyc = kycService.findKYCByUserId(userId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(
            kyc.toResponse(),
            HttpStatus.OK
        )
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(path = ["/kyc/{userId}"])
    fun getKYCByUserId(
        @PathVariable("userId") userId: Long
    ): ResponseEntity<KYCResponse> {
        val kyc = kycService.findKYCByUserId(userId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(
            kyc.toResponse(),
            HttpStatus.OK
        )
    }
}


