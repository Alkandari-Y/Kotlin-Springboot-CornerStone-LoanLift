package com.project.campaignlift.providers

import com.project.common.exceptions.APIException
import com.project.common.enums.ErrorCode
import com.project.common.exceptions.accounts.AccountNotFoundException
import com.project.common.responses.banking.AccountResponse
import com.project.common.responses.banking.UserAccountsResponse
import jakarta.inject.Named
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.web.client.RestTemplate

@Named
class BankServiceProvider(
    @Value("\${bankServiceBase.url}")
    private val bankServiceBaseUrl: String
) {
    val restTemplate = RestTemplate()

    fun getUserAccountsAndProfile(userId: Long, adminToken: String): UserAccountsResponse {
        val url = "$bankServiceBaseUrl/accounts/clients/$userId"
        val response = sendRequest<UserAccountsResponse>(url, adminToken)
        return response.body ?: throw APIException(
            "Failed to fetch user accounts for userId=$userId",
            HttpStatus.INTERNAL_SERVER_ERROR,
            ErrorCode.INTERNAL_SERVER_ERROR
        )
    }

    fun checkAccountExists(accountId: Long, token: String): Boolean {
        val url = "$bankServiceBaseUrl/accounts/$accountId/exists"
        val response = sendRequest<Boolean>(url, token)
        return response.body ?: false
    }

    fun getAccount(accountId: Long, token: String): AccountResponse {
        val url = "$bankServiceBaseUrl/accounts/clients?accountId=$accountId"
        val account = sendRequest<AccountResponse>(url, token).body
            ?: throw AccountNotFoundException()
        return account
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