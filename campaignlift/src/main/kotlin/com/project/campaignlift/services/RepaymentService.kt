package com.project.campaignlift.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.project.banking.entities.TransactionEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.campaignlift.entities.PledgeStatus
import com.project.campaignlift.entities.PledgeTransactionEntity
import com.project.campaignlift.entities.PledgeTransactionType
import com.project.campaignlift.repositories.*
import com.project.common.enums.TransactionType
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class RepaymentService(
    private val campaignRepository: CampaignRepository,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val pledgeTransactionRepository: PledgeTransactionRepository,
    private val pledgeRepository: PledgeRepository,
    private val categoryRepository: CategoryRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(RepaymentService::class.java)

    private val bankFeeRate = BigDecimal("0.002") // 0.2%
    private val scale = 3
    private val roundingMode = RoundingMode.HALF_UP

    @Transactional
    fun processMonthlyRepayments() {
        val campaigns = campaignRepository.findAllFundedCampaigns()
        val categories = categoryRepository.findAllByIdIn(
            campaigns.mapNotNull { it.categoryId }.distinct()
        )
        val categoryMap = categories.associateBy { it.id }


        campaigns.forEach { campaign ->
            val campaignAccount = campaign.accountId?.let { accountRepository.findById(it).orElse(null) }
                ?: return@forEach
            val category = categoryMap[campaign.categoryId]
                ?: return@forEach

            val raisedAmount = pledgeRepository.getTotalCommittedAmountForCampaign(campaign.id!!)
            campaign.amountRaised = raisedAmount.setScale(scale, roundingMode)

            val totalInterest = campaign.amountRaised
                .multiply(campaign.interestRate.divide(BigDecimal(100), scale, roundingMode))

            val monthlyInstallment = (campaign.amountRaised + totalInterest)
                .divide(BigDecimal(campaign.repaymentMonths), scale, roundingMode)
                .abs()

            val bankFee = monthlyInstallment.multiply(bankFeeRate).setScale(scale, roundingMode)
            val distributable = monthlyInstallment - bankFee

            if (campaignAccount.balance < monthlyInstallment) {
                campaignRepository.save(
                    campaign.copy(status = CampaignStatus.DEFAULTED)
                )
                logger.warn("Campaign ${campaign.id} defaulted due to insufficient balance")
                // TODO: Notify admin
                return@forEach
            }

            val pledges = campaign.pledges.filter { it.status == PledgeStatus.COMMITTED }
            val totalPledged = pledges.sumOf { it.amount }

            // Deduct installment from campaign account
            val newCampaignBalance = campaignAccount.balance - monthlyInstallment
            accountRepository.save(campaignAccount.copy(balance = newCampaignBalance))

            pledges.forEach { pledge ->
                val ratio = pledge.amount.divide(totalPledged, 6, roundingMode)
                val amountToDistribute = distributable.multiply(ratio).setScale(scale, roundingMode)
                val pledgerAccount = accountRepository.findByIdOrNull(pledge.accountId) ?: return@forEach
                val updatedPledgerBalance = pledgerAccount.balance + amountToDistribute
                accountRepository.save(pledgerAccount.copy(balance = updatedPledgerBalance))

                val transaction = transactionRepository.save(
                    TransactionEntity(
                        sourceAccount = campaignAccount,
                        destinationAccount = pledgerAccount,
                        amount = amountToDistribute,
                        type = TransactionType.REPAYMENT,
                        category = category
                    )
                )

                pledgeTransactionRepository.save(
                    PledgeTransactionEntity(
                        transactionId = transaction.id!!,
                        pledge = pledge,
                        type = PledgeTransactionType.REPAYMENT
                    )
                )
            }
        }
    }
}
