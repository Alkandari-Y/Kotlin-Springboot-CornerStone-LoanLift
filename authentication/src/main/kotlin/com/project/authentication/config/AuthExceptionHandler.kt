package com.project.authentication.config

import com.project.authentication.exceptions.InvalidCredentialsException
import com.project.authentication.exceptions.UserExistsException
import com.project.common.exceptions.ApiErrorResponse
import com.project.common.exceptions.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import java.time.Instant

@RestControllerAdvice(
    basePackages = ["com.project.authentication"]
)
class AuthExceptionHandler {
    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleUserAlreadyExists(
        ex: InvalidCredentialsException,
        request: WebRequest
    ): ResponseEntity<ApiErrorResponse> {
        val status = HttpStatus.UNAUTHORIZED
        val errorResponse = ApiErrorResponse(
            timestamp = Instant.now().toString(),
            status = status.value(),
            error = status.reasonPhrase,
            message = "Invalid Credentials",
            code = ErrorCode.INVALID_CREDENTIALS.name,
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.status(status).body(errorResponse)
    }

    @ExceptionHandler(UsernameNotFoundException::class)
    fun handleUsernameNotFoundException(
        ex: UsernameNotFoundException,
        request: WebRequest
    ): ResponseEntity<ApiErrorResponse> {
        val status = HttpStatus.UNAUTHORIZED
        val errorResponse = ApiErrorResponse(
            timestamp = Instant.now().toString(),
            status = status.value(),
            error = status.reasonPhrase,
            message = "Invalid Credentials",
            code = ErrorCode.INVALID_CREDENTIALS.name,
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.status(status).body(errorResponse)
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(
        ex: BadCredentialsException,
        request: WebRequest
    ): ResponseEntity<ApiErrorResponse> {
        val status = HttpStatus.UNAUTHORIZED
        val errorResponse = ApiErrorResponse(
            timestamp = Instant.now().toString(),
            status = status.value(),
            error = status.reasonPhrase,
            message = "Invalid Credentials",
            code = ErrorCode.INVALID_CREDENTIALS.name,
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.status(status).body(errorResponse)
    }

    @ExceptionHandler(UserExistsException::class)
    fun handleUserExistsException(
        ex: BadCredentialsException,
        request: WebRequest
    ): ResponseEntity<ApiErrorResponse> {
        val status = HttpStatus.BAD_REQUEST
        val errorResponse = ApiErrorResponse(
            timestamp = Instant.now().toString(),
            status = status.value(),
            error = status.reasonPhrase,
            message = "User already exists",
            code = ErrorCode.USER_ALREADY_EXISTS.name,
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.status(status).body(errorResponse)
    }
}
