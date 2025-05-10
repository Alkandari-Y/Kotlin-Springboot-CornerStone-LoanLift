package com.project.campaignlift.tasks

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CampaignRepaymentScheduler {

    @Scheduled(cron = "0 0 2 5 * ?", zone = "UTC")
    fun processMonthlyRepayments() {
        println("Running monthly repayment logic...")
    }
}
