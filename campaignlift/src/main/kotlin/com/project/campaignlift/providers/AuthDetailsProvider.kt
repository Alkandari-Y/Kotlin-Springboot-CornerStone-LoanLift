package com.project.campaignlift.providers

import com.project.common.exceptions.APIException
import com.project.common.enums.ErrorCode
import com.project.common.responses.authenthication.UserInfoDto
import jakarta.inject.Named
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.web.client.RestTemplate

@Named
class AuthDetailsProvider(
    @Value("\${authServiceBase.url}")
    private val authServiceBaseUrl: String
) {
    val restTemplate = RestTemplate()

    fun getUserDetailsFromAuth(userId: Long, adminToken: String): UserInfoDto {
        val url = "$authServiceBaseUrl/users/details/$userId"
        val response = sendRequest<UserInfoDto>(url, adminToken)
        return response.body ?: throw APIException(
            "Failed to fetch user details for userId=$userId",
            HttpStatus.INTERNAL_SERVER_ERROR,
            ErrorCode.INTERNAL_SERVER_ERROR
        )
    }

    private inline fun <reified T> sendRequest(
        url: String,
        token: String,
        method: HttpMethod = HttpMethod.GET
    ): ResponseEntity<T> {
        val headers = HttpHeaders().apply {
            setBearerAuth(token.trim())
        }
        val requestEntity = HttpEntity<String>(headers)

        return restTemplate.exchange(
            url,
            method,
            requestEntity,
            object : ParameterizedTypeReference<T>() {}
        )
    }
}