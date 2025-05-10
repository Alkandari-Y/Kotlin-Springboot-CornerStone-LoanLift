package com.project.banking.services

import com.project.common.exceptions.accounts.AccountVerificationException
import com.project.banking.entities.KYCEntity
import com.project.banking.events.KycCreatedEvent
import com.project.banking.kycs.dtos.KYCRequest
import com.project.banking.kycs.dtos.toEntity
import com.project.banking.repositories.KYCRepository
import com.project.common.enums.ErrorCode
import com.project.common.responses.authenthication.UserInfoDto
import com.project.common.utils.dateFormatter
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.Period

@Service
class KYCServiceImpl(
    private val kycRepository: KYCRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val mailService: MailService
): KYCService {

    override fun createKYCOrUpdate(
        kycRequest: KYCRequest,
        user: UserInfoDto
    ): KYCEntity {
        val existingKYC = kycRepository.findByUserId(user.userId)

        val newKycEntity  = existingKYC?.copy(
            firstName= kycRequest.firstName,
            lastName= kycRequest.lastName,
            dateOfBirth= kycRequest.dateOfBirth.let {
                LocalDate.parse(kycRequest.dateOfBirth, dateFormatter)
            },
            nationality= kycRequest.nationality,
            salary= kycRequest.salary,
        )
            ?: kycRequest.toEntity(user.userId)

        val currentDate = LocalDate.now()
        val yearsOfAge = Period.between(newKycEntity.dateOfBirth, currentDate).years
        if (yearsOfAge < 18) throw AccountVerificationException(
            message = "User must be 18 or older",
            code = ErrorCode.INVALID_AGE
        )

        val savedKyc = kycRepository.save(newKycEntity)
        applicationEventPublisher.publishEvent(KycCreatedEvent(savedKyc))
        mailService.sendHtmlEmail(
            to = user.email,
            subject = "Account Activated",
            username = user.username,
            bodyText = "Your account has been activated! Please enjoy our services"
        )
        return savedKyc
    }

    override fun findKYCByUserId(userId: Long): KYCEntity? {
        return kycRepository.findByUserId(userId)
    }
}