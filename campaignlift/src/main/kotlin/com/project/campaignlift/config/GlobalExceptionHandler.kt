package com.project.campaignlift.config

import com.project.common.exceptions.APIException
import com.project.common.exceptions.ApiErrorResponse
import com.project.common.enums.ErrorCode
import com.project.common.exceptions.ValidationError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.time.Instant
import java.time.format.DateTimeFormatter

@ControllerAdvice
class GlobalExceptionHandler {
    //    private val logger = LoggerFactory.getLogger(this::class.java)
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    @ExceptionHandler(APIException::class)
    fun handleAPIBaseException(
        ex: APIException,
        request: WebRequest
    ): ResponseEntity<ApiErrorResponse> {
        val errorResponse = ApiErrorResponse(
            timestamp = Instant.now().toString(),
            status = ex.httpStatus.value(),
            error = ex.httpStatus.reasonPhrase,
            message = ex.message,
            code = ex.code.name,
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.status(ex.httpStatus).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(
        ex: MethodArgumentTypeMismatchException,
        request: WebRequest
    ): ResponseEntity<ApiErrorResponse> {
        val errorResponse = ApiErrorResponse(
            timestamp = Instant.now().toString(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = ex.message,
            code = ErrorCode.INVALID_INPUT.name,
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<ApiErrorResponse> {
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val errorResponse = ApiErrorResponse(
            timestamp = Instant.now().toString(),
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.message ?: "Unexpected error occurred",
            code = ErrorCode.INTERNAL_SERVER_ERROR.name,
            path = request.getDescription(false).removePrefix("uri=")
        )
        println(ex.message)
        println(ex.cause)
        print(ex.stackTrace)
        return ResponseEntity.status(status).body(errorResponse)
    }



    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<ApiErrorResponse> {
        val fieldErrors = ex.bindingResult.fieldErrors.map {
            ValidationError(it.field, it.defaultMessage ?: "Invalid value")
        }

        val status = HttpStatus.BAD_REQUEST

        val errorResponse = ApiErrorResponse(
            timestamp = Instant.now().toString(),
            status = status.value(),
            error = status.reasonPhrase,
            message = "Validation failed",
            code = ErrorCode.INVALID_INPUT.name,
            path = request.getDescription(false).removePrefix("uri="),
            fieldErrors = fieldErrors
        )

        return ResponseEntity.status(status).body(errorResponse)
    }

}