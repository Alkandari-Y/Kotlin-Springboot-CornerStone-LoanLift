package com.project.common.exceptions.campaigns

import com.project.common.exceptions.APIException
import com.project.common.enums.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
data class CampaignNotFoundException(
    override val message: String = "Campaign not found.",
    override val code: ErrorCode = ErrorCode.CAMPAIGN_NOT_FOUND,
    override val httpStatus: HttpStatus = HttpStatus.NOT_FOUND,
    override val cause: Throwable? = null
) : APIException(message, httpStatus, code, cause)
