package com.project.common.exceptions.kycs

import com.project.common.exceptions.APIException
import com.project.common.enums.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
data class KycNotFoundException(
    val userId: Long,
    override val cause: Throwable? = null
) : APIException(
    message = "KYC record for user ID $userId not found.",
    httpStatus = HttpStatus.NOT_FOUND,
    code = ErrorCode.KYC_NOT_FOUND,
    cause = cause
)