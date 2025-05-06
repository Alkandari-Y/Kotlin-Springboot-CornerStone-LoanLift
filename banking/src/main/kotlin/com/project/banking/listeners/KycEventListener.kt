package com.project.banking.listeners

import com.project.banking.events.KycCreatedEvent
import com.project.common.responses.authenthication.ValidateTokenResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.event.EventListener
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

@Component
class ProfileEventListener(
    @Value("\${authServiceBase.url}")
    private val authServiceURL: String
) {

    @EventListener
    fun handleProfileCreated(event: KycCreatedEvent) {
        val profile = event.profile
        println("Kyc created event received: $profile")
        val restTemplate = RestTemplate()
        val api = "$authServiceURL/users/set-active/${profile.userId}"
        val response = restTemplate.postForEntity(api, null, Boolean::class.java)
        println(response.body)

    }

}

