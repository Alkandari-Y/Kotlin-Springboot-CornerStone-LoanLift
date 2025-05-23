package com.project.campaignlift.tasks

import com.project.campaignlift.services.RepaymentService
import jakarta.transaction.Transactional
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CampaignRepaymentScheduler(
    private val repaymentService: RepaymentService
) {

    // Runs at 2:00 AM on the 22nd day of every month (Asia/Kuwait time)
    // Second Minute Hours DayOfMonth Month DayOfWeek
    //   0      0       0       0       0       ?
//    @Scheduled(cron = "0 30 14 13 5 ?", zone = "Asia/Kuwait") // for demo
    @Scheduled(cron = "0 0 11 22 * ?", zone = "Asia/Kuwait")
    fun processMonthlyRepayments() {
        println("[Scheduler] Running monthly repayment")
        repaymentService.processMonthlyRepayments()
    }

    @Scheduled(cron = "0 29 23 * * *", zone = "Asia/Kuwait") // Runs daily at 2 AM
    @Transactional
    fun processMonthlyCampaignFundingDeadlines() {
        println("[Scheduler] Running monthly repayment")
        repaymentService.processCampaignFailures()
    }
}
