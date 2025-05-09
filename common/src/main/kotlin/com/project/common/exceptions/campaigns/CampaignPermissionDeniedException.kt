package com.project.common.exceptions.campaigns

import com.project.common.exceptions.APIException
import com.project.common.enums.ErrorCode
import org.springframework.http.HttpStatus


data class CampaignPermissionDeniedException(
    val userId: Long,
    val campaignId: Long,
    override val cause: Throwable? = null
) : APIException(
    message = "User $userId is not allowed to update or delete campaign $campaignId.",
    httpStatus = HttpStatus.FORBIDDEN,
    code = ErrorCode.UNAUTHORIZED,
    cause = cause
)