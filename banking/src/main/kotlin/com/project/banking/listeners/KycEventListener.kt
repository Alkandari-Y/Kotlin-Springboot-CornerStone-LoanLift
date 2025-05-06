package com.project.banking.listeners

import com.project.banking.events.KycCreatedEvent
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class ProfileEventListener(
    @Value("\${authServiceBase.url}")
    private val authServiceURL: String
) {

    @EventListener
    fun handleProfileCreated(event: KycCreatedEvent) {
        val profile = event.profile
        val restTemplate = RestTemplate()
        val api = "$authServiceURL/users/set-active/${profile.userId}"
        restTemplate.postForEntity(api, null, Boolean::class.java)
    }
}

