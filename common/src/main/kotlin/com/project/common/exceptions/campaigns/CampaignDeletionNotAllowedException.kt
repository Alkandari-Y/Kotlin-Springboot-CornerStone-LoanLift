package com.project.common.exceptions.campaigns

import com.project.common.exceptions.APIException
import com.project.common.enums.ErrorCode
import org.springframework.http.HttpStatus


data class CampaignDeletionNotAllowedException(
    val campaignId: Long,
    val status: String,
    override val cause: Throwable? = null
) : APIException(
    message = "Campaign $campaignId cannot be deleted as its status is $status.",
    httpStatus = HttpStatus.BAD_REQUEST,
    code = ErrorCode.CAMPAIGN_ALREADY_STARTED,
    cause = cause
)