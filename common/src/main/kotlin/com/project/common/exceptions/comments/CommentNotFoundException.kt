package com.project.common.exceptions.comments

import com.project.common.enums.ErrorCode
import com.project.common.exceptions.APIException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(HttpStatus.NOT_FOUND)
data class CommentNotFoundException(
    override val cause: Throwable? = null
) : APIException(
    message = "Comment not found.",
    httpStatus = HttpStatus.BAD_REQUEST,
    code = ErrorCode.COMMENT_NOT_FOUND,
    cause = cause
)