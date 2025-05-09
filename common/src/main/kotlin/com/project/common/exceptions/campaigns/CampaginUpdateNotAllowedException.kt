package com.project.common.exceptions.campaigns

import com.project.common.enums.ErrorCode
import com.project.common.exceptions.APIException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
data class CampaignUpdateNotAllowedException(
    val status: String,
    override val cause: Throwable? = null
) : APIException(
    message = "Campaign cannot be updated while in status: $status.",
    httpStatus = HttpStatus.BAD_REQUEST,
    code = ErrorCode.INVALID_INPUT,
    cause = cause
)
