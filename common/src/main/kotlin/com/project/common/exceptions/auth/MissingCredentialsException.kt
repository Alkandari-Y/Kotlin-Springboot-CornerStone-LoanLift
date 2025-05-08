package com.project.common.exceptions.auth

import com.project.common.exceptions.APIException
import com.project.common.enums.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED)
data class MissingCredentialsException(
    override val message: String = "Missing or invalid credentials.",
    override val code: ErrorCode = ErrorCode.INVALID_CREDENTIALS,
    override val httpStatus: HttpStatus = HttpStatus.UNAUTHORIZED,
    override val cause: Throwable? = null
) : APIException(message, httpStatus, code, cause)