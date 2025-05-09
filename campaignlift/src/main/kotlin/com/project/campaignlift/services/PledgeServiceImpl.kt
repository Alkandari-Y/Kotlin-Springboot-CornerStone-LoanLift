package com.project.campaignlift.services

import com.project.banking.entities.AccountEntity
import com.project.banking.entities.TransactionEntity
import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.campaignlift.entities.PledgeEntity
import com.project.campaignlift.entities.PledgeStatus
import com.project.campaignlift.entities.PledgeTransactionEntity
import com.project.campaignlift.entities.PledgeTransactionType
import com.project.campaignlift.pledges.dtos.*
import com.project.campaignlift.repositories.AccountRepository
import com.project.campaignlift.repositories.CampaignRepository
import com.project.campaignlift.repositories.CategoryRepository
import com.project.campaignlift.repositories.PledgeRepository
import com.project.campaignlift.repositories.PledgeTransactionRepository
import com.project.campaignlift.repositories.TransactionRepository
import com.project.common.enums.TransactionType
import com.project.common.exceptions.AccessDeniedException
import com.project.common.exceptions.accounts.AccountNotFoundException
import com.project.common.exceptions.accounts.AccountVerificationException
import com.project.common.exceptions.accounts.InsufficientFundsException
import com.project.common.exceptions.campaigns.CampaignNotFoundException
import com.project.common.exceptions.categories.CategoryNotFoundException
import com.project.common.exceptions.kycs.AccountNotVerifiedException
import com.project.common.exceptions.pledges.InvalidPledgeOperationException
import com.project.common.exceptions.pledges.PledgeNotFoundException
import com.project.common.responses.authenthication.UserInfoDto
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate


@Service
class PledgeServiceImpl(
    private val pledgeRepository: PledgeRepository,
    private val campaignRepository: CampaignRepository,
    private val pledgeTransactionRepository: PledgeTransactionRepository,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
): PledgeService {
    override fun getPledgeTransactions(pledgeId: Long): List<PledgeTransactionWithDetails> {
        return pledgeTransactionRepository.findDetailsByPledgeId(pledgeId)
    }

    override fun getPledgeDetails(pledgeId: Long): PledgeEntity {
        return pledgeRepository.findByIdWithTransactions(pledgeId) ?: throw PledgeNotFoundException()
    }



    override fun getAllUserPledges(userId: Long): List<UserPledgeDto> {
        return pledgeRepository.findAllByUserIdDto(userId)
    }

    private val MIN_PLEDGE_AMOUNT = BigDecimal.valueOf(1000).setScale(3)

    @Transactional
    override fun createPledge(
        userInfo: UserInfoDto,
        accountId: Long,
        campaignId: Long,
        amount: BigDecimal
    ): PledgeResultDto {
        if (amount < MIN_PLEDGE_AMOUNT) {
            throw InvalidPledgeOperationException("Pledge amount must be greater than $MIN_PLEDGE_AMOUNT.")
        }

        val campaign = campaignRepository.findById(campaignId)
            .orElseThrow { IllegalArgumentException("Campaign not found") }

        validateCampaignIsPledgeable(campaign)
        validatePledgerIsNotCampaignOwner(campaign, userInfo.userId)

        val userAccount = accountRepository.findByIdOrNull(accountId)
            ?: throw AccountNotFoundException()

        validateUserAccount(userAccount, userInfo)
        validateSufficientFunds(userAccount, amount)

        val campaignAccount = campaign.accountId?.let { accountRepository.findById(it) }
            ?.orElseThrow { CampaignNotFoundException() }
            ?: throw AccountNotFoundException("Campaign has no valid funding account.")

        val existingPledge = pledgeRepository.findByUserIdAndCampaignId(userInfo.userId, campaignId)

        val pledge = when {
            existingPledge == null -> {
                PledgeEntity(
                    userId = userInfo.userId,
                    accountId = accountId,
                    campaign = campaign,
                    amount = amount,
                    createdAt = LocalDate.now(),
                    updatedAt = LocalDate.now(),
                    commitedAt = LocalDate.now(),
                    status = PledgeStatus.COMMITTED
                )
            }
            existingPledge.status == PledgeStatus.WITHDRAWN -> {
                existingPledge.copy(
                    amount = amount,
                    accountId = accountId,
                    status = PledgeStatus.COMMITTED,
                    withdrawnAt = null,
                    updatedAt = LocalDate.now(),
                    commitedAt = LocalDate.now()
                )
            }
            else -> throw InvalidPledgeOperationException("You already have an active pledge for this campaign.")
        }

        val category = categoryRepository.findByIdOrNull(campaign.categoryId!!)
            ?: throw CategoryNotFoundException()

        val bankingTransaction = transactionRepository.save(
            TransactionEntity(
                sourceAccount = userAccount,
                destinationAccount = campaignAccount,
                amount = amount,
                type = TransactionType.PLEDGE,
                category = category
            )
        )

        val newPledgerBalance = userAccount.balance.setScale(3).subtract(amount)
        val newCampaignAccountBalance = campaignAccount.balance.setScale(3).add(amount)

        accountRepository.saveAll(
            listOf(
                userAccount.copy(balance = newPledgerBalance),
                campaignAccount.copy(balance = newCampaignAccountBalance)
            )
        )

        val savedPledge = pledgeRepository.save(pledge)

        val pledgeTransaction = pledgeTransactionRepository.save(
            PledgeTransactionEntity(
                transactionId = bankingTransaction.id!!,
                pledge = savedPledge,
                type = PledgeTransactionType.FUNDING
            )
        )

        val amountRaised = pledgeRepository.getTotalCommittedAmountForCampaign(campaignId = campaign.id!!)
        if (campaign.amountRaised <= amountRaised) {
            campaignRepository.save(campaign.copy(status = CampaignStatus.ACTIVE))
        }

        return PledgeResultDto(
            pledge = savedPledge.toUserPledgeDto(campaign.title, campaign.id!!),
            transaction = pledgeTransaction.toResultDto()
        )
    }

    override fun updatePledge(
        pledgeId: Long,
        userId: Long,
        newAmount: BigDecimal
    ): PledgeResultDto {
        if (newAmount < MIN_PLEDGE_AMOUNT) {
            throw InvalidPledgeOperationException("Pledge amount must be greater than $MIN_PLEDGE_AMOUNT.")
        }

        val pledge = pledgeRepository.findById(pledgeId)
            .orElseThrow { IllegalArgumentException("Pledge not found") }

        if (pledge.userId != userId) {
            throw AccessDeniedException("You cannot modify this pledge.")
        }

        val previousAmount = pledge.amount
        val delta = newAmount.subtract(previousAmount)

        if (delta == BigDecimal.ZERO) {
            throw InvalidPledgeOperationException("New amount must be different from current amount.")
        }

        if (delta < MIN_PLEDGE_AMOUNT) {
            throw InvalidPledgeOperationException("Pledge amount must be greater than $MIN_PLEDGE_AMOUNT.")
        }

        val userAccount = accountRepository.findByIdOrNull(pledge.accountId)
            ?: throw AccountNotFoundException()

        if (delta > BigDecimal.ZERO) {
            validateSufficientFunds(userAccount, delta)
        }

        val campaign = pledge.campaign
        validateCampaignIsPledgeable(campaign)


        val campaignAccount = campaign.accountId?.let { accountRepository.findById(it) }
            ?.orElseThrow { CampaignNotFoundException() }
            ?: throw AccountNotFoundException("Campaign has no valid funding account.")

        val transactionType = if (delta > BigDecimal.ZERO) TransactionType.PLEDGE else TransactionType.REFUND
        val pledgeTransactionType = if (delta > BigDecimal.ZERO) PledgeTransactionType.FUNDING else PledgeTransactionType.REFUND

        val category = categoryRepository.findByIdOrNull(campaign.categoryId!!)
            ?: throw CategoryNotFoundException()

        val transaction = transactionRepository.save(
            TransactionEntity(
                sourceAccount = userAccount,
                destinationAccount = campaignAccount,
                amount = delta.abs(),
                type = transactionType,
                category = category
            )
        )

        if (delta > BigDecimal.ZERO) {
            accountRepository.save(userAccount.copy(balance = userAccount.balance - delta))
            accountRepository.save(campaignAccount.copy(balance = campaignAccount.balance + delta))
        } else {
            accountRepository.save(userAccount.copy(balance = userAccount.balance + delta.abs()))
            accountRepository.save(campaignAccount.copy(balance = campaignAccount.balance - delta.abs()))
        }


        val updatedPledge = pledge.copy(
            amount = newAmount,
            updatedAt = LocalDate.now()
        )
        val savedPledge = pledgeRepository.save(updatedPledge)

        val pledgeTransaction = pledgeTransactionRepository.save(
            PledgeTransactionEntity(
                transactionId = transaction.id!!,
                pledge = savedPledge,
                type = pledgeTransactionType
            )
        )
        val amountRaised = pledgeRepository.getTotalCommittedAmountForCampaign(campaignId = campaign.id!!)
        if (campaign.amountRaised <= amountRaised) {
            campaignRepository.save(campaign.copy(status = CampaignStatus.ACTIVE))
        }
        return PledgeResultDto(
            pledge = savedPledge.toUserPledgeDto(
                title = campaign.title,
                campaignId = campaign.id!!
            ),
            transaction = pledgeTransaction.toResultDto()
        )
    }

    @Transactional
    override fun withdrawPledge(pledgeId: Long, userId: Long) {
        val pledge = pledgeRepository.findById(pledgeId)
            .orElseThrow { IllegalArgumentException("Pledge not found") }

        if (pledge.userId != userId) {
            throw AccessDeniedException("You cannot withdraw someone else's pledge.")
        }

        val campaign = pledge.campaign

        if (campaign.status != CampaignStatus.ACTIVE) {
            throw InvalidPledgeOperationException("Only pledges on Active campaigns can be withdrawn.")
        }


        if (campaign.campaignDeadline != null && campaign.campaignDeadline!!.isBefore(LocalDate.now())) {
            throw InvalidPledgeOperationException("Cannot withdraw pledge after campaign deadline.")
        }
        val campaignAccount = campaign.accountId?.let { accountRepository.findById(it) }
            ?.orElseThrow { CampaignNotFoundException() }
            ?: throw AccountNotFoundException("Campaign has no valid funding account.")
        val pledgerAccount = accountRepository.findByIdOrNull(pledge.accountId)
            ?: throw AccountNotFoundException("Campaign has no valid funding account.")

        val category = categoryRepository.findByIdOrNull(campaign.categoryId!!)
            ?: throw CategoryNotFoundException()

        val updated = pledge.copy(
            amount = BigDecimal.ZERO,
            status = PledgeStatus.WITHDRAWN,
            updatedAt = LocalDate.now(),
            withdrawnAt = LocalDate.now()
        )


        accountRepository.saveAll(
            listOf(
                accountRepository.save(pledgerAccount.copy(balance = pledgerAccount.balance + pledge.amount.abs())),
                accountRepository.save(campaignAccount.copy(balance = campaignAccount.balance - pledge.amount.abs()))
            )
        )
        val savedTransaction = transactionRepository.save(
            TransactionEntity(
                sourceAccount = campaignAccount,
                destinationAccount = pledgerAccount,
                amount = pledge.amount.abs(),
                type = TransactionType.REFUND,
                category = category
            )
        )

        pledgeTransactionRepository.save(
            PledgeTransactionEntity(
                transactionId = savedTransaction.id!!,
                pledge = updated,
                type = PledgeTransactionType.REFUND
            )
        )
        pledgeRepository.save(updated)
    }


    private fun validateCampaignIsPledgeable(campaign: CampaignEntity) {
        if (campaign.status != CampaignStatus.ACTIVE || campaign.campaignDeadline?.isBefore(LocalDate.now()) == true) {
            throw InvalidPledgeOperationException("Campaign is not accepting pledges at this time.")
        }
    }

    private fun validateUserAccount(account: AccountEntity, user: UserInfoDto): Boolean {
        if (account.ownerId != user.userId) {
            throw AccountVerificationException("You do not own this account.")
        }

        if (!user.isActive) {
            throw AccountNotVerifiedException()
        }
        return true
    }

    private fun validateSufficientFunds(account: AccountEntity, amount: BigDecimal): Boolean {
        if (account.balance < amount) {
            throw InsufficientFundsException("Insufficient funds to complete pledge.")
        }
        return true
    }

    private fun validatePledgerIsNotCampaignOwner(campaign: CampaignEntity, userId: Long): Boolean {
        if (campaign.createdBy == userId) {
            throw InvalidPledgeOperationException("Campaign creators cannot pledge to their own campaigns.")
        }
        return true
    }
}