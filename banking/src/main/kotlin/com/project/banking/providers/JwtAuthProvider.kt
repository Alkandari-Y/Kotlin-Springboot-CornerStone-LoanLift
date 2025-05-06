package com.project.banking.providers

import com.project.common.responses.authenthication.ValidateTokenResponse
import jakarta.inject.Named
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

@Named
class JwtAuthProvider (
    @Value("\${authServiceBase.url}")
    private val authServiceURL: String
){
    fun authenticateToken(token: String): ValidateTokenResponse {
        val restTemplate = RestTemplate()
        val response = restTemplate.exchange<ValidateTokenResponse>(
            url = "$authServiceURL/auth/validate",
            method = HttpMethod.POST,
            requestEntity = HttpEntity<String>(
                MultiValueMap.fromMultiValue(mapOf("Authorization" to listOf("Bearer $token")))
            ),
            object : ParameterizedTypeReference<ValidateTokenResponse>() {
            }
        )
        return response.body ?: throw IllegalStateException("Check token response has no body ...")
    }
}