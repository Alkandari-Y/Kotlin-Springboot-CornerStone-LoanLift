package com.project.banking.accounts.exceptions

import com.project.common.exceptions.APIException
import com.project.common.exceptions.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InsufficientFundsException(
    override val message: String = "Insufficient balance.",
    override val code: ErrorCode = ErrorCode.INVALID_FUNDS,
    override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    override val cause: Throwable? = null
) : APIException(message, httpStatus, code, cause)