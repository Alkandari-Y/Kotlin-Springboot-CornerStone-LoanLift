package com.project.banking.accounts.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidTransferException(
    override val message: String = "Cannot transfer to the same account.",
    override val code: ErrorCode = ErrorCode.INVALID_TRANSFER,
    override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    override val cause: Throwable? = null
) : APIBaseException(message, httpStatus, code, cause)