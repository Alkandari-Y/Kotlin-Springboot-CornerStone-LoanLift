package com.project.banking.services

import com.project.banking.accounts.exceptions.AccountVerificationException
import com.project.banking.entities.KYCEntity
import com.project.banking.entities.kycDateFormatter
import com.project.banking.events.KycCreatedEvent
import com.project.banking.kycs.dtos.KYCRequest
import com.project.banking.kycs.dtos.toEntity
import com.project.banking.repositories.KYCRepository
import com.project.common.exceptions.ErrorCode
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.Period

@Service
class KYCServiceImpl(
    private val kycRepository: KYCRepository,
    private val applicationEventPublisher: ApplicationEventPublisher

): KYCService {

    override fun createKYCOrUpdate(
        kycRequest: KYCRequest,
        userId: Long
    ): KYCEntity {
        val existingKYC = kycRepository.findByUserId(userId)

        val newKycEntity  = existingKYC?.copy(
            firstName= kycRequest.firstName,
            lastName= kycRequest.lastName,
            dateOfBirth= kycRequest.dateOfBirth.let {
                LocalDate.parse(kycRequest.dateOfBirth, kycDateFormatter)
            },
            nationality= kycRequest.nationality,
            salary= kycRequest.salary,
        )
            ?: kycRequest.toEntity(userId)

        val currentDate = LocalDate.now()
        val yearsOfAge = Period.between(newKycEntity.dateOfBirth, currentDate).years
        if (yearsOfAge < 18) throw AccountVerificationException(
            message = "User must be 18 or older",
            code = ErrorCode.INVALID_AGE
        )

        val savedKyc = kycRepository.save(newKycEntity)
        applicationEventPublisher.publishEvent(KycCreatedEvent(savedKyc))
        return savedKyc
    }

    override fun findKYCByUserId(userId: Long): KYCEntity? {
        return kycRepository.findByUserId(userId)
    }
}