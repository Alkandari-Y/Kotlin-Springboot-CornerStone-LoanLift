package com.project.authentication.exceptions

import com.project.common.exceptions.APIException
import com.project.common.exceptions.ErrorCode
import org.springframework.http.HttpStatus

class UserNotFoundException(
    override val message: String = "User Not Found",
    override val httpStatus: HttpStatus = HttpStatus.NOT_FOUND,
    override val code: ErrorCode = ErrorCode.USER_NOT_FOUND,
    override val cause: Throwable? = null
) : APIException(message, httpStatus, code, cause)