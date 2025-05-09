package com.project.common.exceptions.campaigns

import com.project.common.exceptions.APIException
import com.project.common.enums.ErrorCode
import org.springframework.http.HttpStatus

data class CampaignOwnerNotFoundException(
    val campaignId: Long,
    override val cause: Throwable? = null
) : APIException(
    message = "Owner of campaign with id $campaignId not found.",
    httpStatus = HttpStatus.NOT_FOUND,
    code = ErrorCode.USER_NOT_FOUND,
    cause = cause
)