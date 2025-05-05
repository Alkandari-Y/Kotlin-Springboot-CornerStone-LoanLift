package com.project.authentication.exceptions

import com.project.common.exceptions.APIException
import com.project.common.exceptions.ErrorCode
import org.springframework.http.HttpStatus

class InvalidCredentialsException(
    override val message: String = "Invalid credentials",
    override val httpStatus: HttpStatus = HttpStatus.UNAUTHORIZED,
    override val code: ErrorCode = ErrorCode.INVALID_CREDENTIALS,
    override val cause: Throwable? = null
) : APIException(message, httpStatus, code, cause)