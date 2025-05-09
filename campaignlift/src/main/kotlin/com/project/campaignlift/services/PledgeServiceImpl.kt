package com.project.campaignlift.services

import com.project.banking.entities.AccountEntity
import com.project.banking.entities.TransactionEntity
import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.campaignlift.entities.PledgeEntity
import com.project.campaignlift.entities.PledgeStatus
import com.project.campaignlift.entities.PledgeTransactionEntity
import com.project.campaignlift.entities.PledgeTransactionType
import com.project.campaignlift.pledges.dtos.PledgeResultDto
import com.project.campaignlift.pledges.dtos.UserPledgeDto
import com.project.campaignlift.pledges.dtos.toResultDto
import com.project.campaignlift.pledges.dtos.toUserPledgeDto
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
    override fun getAllUserPledges(userId: Long): List<UserPledgeDto> {
        return pledgeRepository.findAllByUserIdDto(userId)
    }

    @Transactional
    override fun createPledge(
        user: UserInfoDto,
        accountId: Long,
        campaignId: Long,
        amount: BigDecimal
    ): PledgeResultDto {
        val campaign = campaignRepository.findById(campaignId)
            .orElseThrow { IllegalArgumentException("Campaign not found") }

        validateCampaignIsPledgeable(campaign)
        validatePledgerIsNotCampaignOwner(campaign, user.userId)

        val userAccount = accountRepository.findByIdOrNull(accountId)
            ?: throw AccountNotFoundException()

        validateUserAccount(userAccount, user)
        validateSufficientFunds(userAccount, amount)

        val campaignAccount = campaign.accountId?.let { accountRepository.findById(it) }
            ?.orElseThrow { CampaignNotFoundException() }
            ?: throw InvalidPledgeOperationException("Campaign has no valid funding account.")

        val existingPledge = pledgeRepository.findByUserIdAndCampaignId(user.userId, campaignId)

        val pledge = when {
            existingPledge == null -> {
                PledgeEntity(
                    userId = user.userId,
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

        return PledgeResultDto(
            pledge = savedPledge.toUserPledgeDto(campaign.title, campaign.id!!),
            transaction = pledgeTransaction.toResultDto()
        )
    }


    // update pledge uses the same old pledge finds an old one with status withdrawn and updates status
    // calls bank to perform transaction
    //
    override fun updatePledge(
        pledgeId: Long,
        userId: Long,
        newAmount: BigDecimal
    ): PledgeResultDto {
        val pledge = pledgeRepository.findById(pledgeId)
            .orElseThrow { IllegalArgumentException("Pledge not found") }

        if (pledge.userId != userId) {
            throw AccessDeniedException("You cannot modify this pledge.")
        }

        val campaign = pledge.campaign
        validateCampaignIsPledgeable(campaign)

        val previousAmount = pledge.amount
        val delta = newAmount.subtract(previousAmount)

        if (newAmount <= BigDecimal.ZERO) {
            throw InvalidPledgeOperationException("Pledge amount must be greater than 0.")
        }

        val transactionType = when {
            delta > BigDecimal.ZERO -> PledgeTransactionType.FUNDING
            delta < BigDecimal.ZERO -> PledgeTransactionType.REFUND
            else -> throw InvalidPledgeOperationException("New amount must be different from current amount.")
        }

        val transaction = PledgeTransactionEntity(
            transactionId = 1L,
            pledge = pledge,
            type = transactionType
        )

        val updatedPledge = pledge.copy(
            amount = newAmount,
            updatedAt = LocalDate.now()
        )

        val pledgeDto = pledgeRepository.save(updatedPledge).toUserPledgeDto(
            title = campaign.title,
            campaignId = campaign.id!!
        )
        val transactionDto = pledgeTransactionRepository
                .save(transaction)
                .toResultDto()

        return PledgeResultDto(pledgeDto, transactionDto)
    }

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

        val updated = pledge.copy(
            status = PledgeStatus.WITHDRAWN,
            updatedAt = LocalDate.now(),
            withdrawnAt = LocalDate.now()
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