package com.project.common.exceptions.comments

import com.project.common.enums.ErrorCode
import com.project.common.exceptions.APIException
import org.springframework.http.HttpStatus


data class CommentDeleteException(
    override val cause: Throwable? = null,
    override val message: String = "Comment cannot be deleted.",
    override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    override val code: ErrorCode = ErrorCode.REPLY_ALREADY_EXISTS
) : APIException(
    message = message,
    httpStatus = httpStatus,
    code = code,
    cause = cause
)