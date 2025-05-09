package com.project.common.exceptions.comments

import com.project.common.enums.ErrorCode
import com.project.common.exceptions.APIException
import org.springframework.http.HttpStatus


data class InvalidReplyAuthorException(
    override val cause: Throwable? = null
) : APIException(
    message = "Restricted to campaign owner",
    httpStatus = HttpStatus.FORBIDDEN,
    code = ErrorCode.INVALID_CREDENTIALS,
    cause = cause
)
