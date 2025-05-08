package com.project.common.exceptions.campaigns

import com.project.common.exceptions.APIException
import org.springframework.web.bind.annotation.ResponseStatus

import com.project.common.enums.ErrorCode
import org.springframework.http.HttpStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
data class InvalidCampaignStatusChangeException(
    val invalidStatus: String,
    override val cause: Throwable? = null
) : APIException(
    message = "Invalid campaign status change: $invalidStatus",
    httpStatus = HttpStatus.BAD_REQUEST,
    code = ErrorCode.INVALID_INPUT,
    cause = cause
)
