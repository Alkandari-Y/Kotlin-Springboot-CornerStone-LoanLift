package com.project.campaignlift.services

import com.project.banking.entities.CategoryEntity
import org.slf4j.LoggerFactory
import com.project.banking.entities.TransactionEntity
import com.project.campaignlift.entities.*
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

    private val bankFeeRate = BigDecimal("0.002")
    private val scale = 3
    private val roundingMode = RoundingMode.HALF_UP
    private val logger = LoggerFactory.getLogger(RepaymentService::class.java)

    @Transactional
    fun processMonthlyRepayments() {
        val campaigns = campaignRepository.findAllFundedCampaigns()
        val categoryMap = preloadCategoryMap()

        campaigns.forEach { campaign ->
            processSingleCampaignRepayment(campaign, categoryMap)
        }
    }

    fun processSingleCampaignRepayment(
        campaign: CampaignEntity,
        categoryMap: Map<Long?, CategoryEntity>
    ) {
        val campaignAccount = campaign.accountId?.let { accountRepository.findById(it).orElse(null) }
            ?: return

        val category = categoryMap[campaign.category?.id] ?: return

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
            logger.warn("Campaign ${campaign.id} defaulted. Insufficient balance.")
            campaignRepository.save(campaign.copy(status = CampaignStatus.DEFAULTED))
            return
        }

        val pledges = campaign.pledges.filter { it.status == PledgeStatus.COMMITTED }
        val totalPledged = pledges.sumOf { it.amount }

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

    private fun preloadCategoryMap(): Map<Long?, CategoryEntity> {
        return categoryRepository.findAll().associateBy { it.id }
    }
}
