package com.project.common.exceptions.pledges

import com.project.common.enums.ErrorCode
import com.project.common.exceptions.APIException
import org.springframework.http.HttpStatus

class PledgeNotFoundException (
    override val message: String = "Pledge not found.",
    override val code: ErrorCode = ErrorCode.INVALID_INPUT,
    override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST
) : APIException(message, httpStatus, code)
