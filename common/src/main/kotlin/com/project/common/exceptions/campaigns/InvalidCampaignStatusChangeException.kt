package com.project.common.exceptions.campaigns

import com.project.common.exceptions.APIException
import com.project.common.enums.ErrorCode
import org.springframework.http.HttpStatus

data class InvalidCampaignStatusChangeException(
    val invalidStatus: String,
    override val cause: Throwable? = null
) : APIException(
    message = "Invalid campaign status change: $invalidStatus",
    httpStatus = HttpStatus.BAD_REQUEST,
    code = ErrorCode.INVALID_INPUT,
    cause = cause
)
