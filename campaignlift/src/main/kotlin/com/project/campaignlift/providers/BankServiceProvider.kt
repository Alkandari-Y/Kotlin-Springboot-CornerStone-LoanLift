package com.project.campaignlift.providers

import com.project.common.responses.banking.UserAccountsResponse
import jakarta.inject.Named
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.http.HttpHeaders

@Named
class BandServiceProvider (
    @Value("\${bankServiceBase.url}")
    private val authServiceURL: String
){
    fun getUserAccountsAndProfile(userId: Long, adminToken: String): UserAccountsResponse {
        val restTemplate = RestTemplate()
        val headers = HttpHeaders().apply {
            setBearerAuth(adminToken.trim())
        }
        val requestEntity = HttpEntity<String>(headers)

        val response = restTemplate.exchange<UserAccountsResponse>(
            url = "$authServiceURL/accounts/clients/$userId",
            method = HttpMethod.GET,
            requestEntity = requestEntity,
            object : ParameterizedTypeReference<UserAccountsResponse>() {
            }
        )
        return response.body ?: throw IllegalStateException("Get user details response has no body ...")
    }
}