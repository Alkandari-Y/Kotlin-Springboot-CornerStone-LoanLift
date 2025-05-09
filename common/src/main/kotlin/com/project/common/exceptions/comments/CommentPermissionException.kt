package com.project.common.exceptions.comments

import com.project.common.enums.ErrorCode
import com.project.common.exceptions.APIException
import org.springframework.http.HttpStatus


data class CommentPermissionException(
    override val cause: Throwable? = null,
    override val message: String = "You lack permissions to perform this action",
    override val httpStatus: HttpStatus = HttpStatus.UNAUTHORIZED,
    override val code: ErrorCode = ErrorCode.INVALID_CREDENTIALS
) : APIException(
    message = message,
    httpStatus = httpStatus,
    code = code,
    cause = cause
)