package com.project.common.exceptions.comments

import com.project.common.enums.ErrorCode
import com.project.common.exceptions.APIException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(HttpStatus.BAD_REQUEST)
data class ReplyAlreadyExistsException(
    override val cause: Throwable? = null
) : APIException(
    message = "Reply already exists.",
    httpStatus = HttpStatus.BAD_REQUEST,
    code = ErrorCode.REPLY_ALREADY_EXISTS,
    cause = cause
)