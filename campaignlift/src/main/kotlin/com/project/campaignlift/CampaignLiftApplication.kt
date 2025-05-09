package com.project.campaignlift

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


@SpringBootApplication
@EntityScan(
    basePackages = [
        "com.project.campaignlift.entities",
        "com.project.banking.entities"
    ]
)
@EnableJpaRepositories(
    basePackages = [
        "com.project.campaignlift.repositories",
    ]
)
class CampaignLiftApplication

fun main(args: Array<String>) {
    runApplication<CampaignLiftApplication>(*args)
}