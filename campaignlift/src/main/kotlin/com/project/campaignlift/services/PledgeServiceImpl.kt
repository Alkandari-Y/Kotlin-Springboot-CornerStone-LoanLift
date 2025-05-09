package com.project.campaignlift.services

import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.campaignlift.entities.PledgeEntity
import com.project.campaignlift.entities.PledgeStatus
import com.project.campaignlift.entities.PledgeTransactionEntity
import com.project.campaignlift.entities.PledgeTransactionType
import com.project.campaignlift.entities.projections.UserPledgeProjection
import com.project.campaignlift.pledges.dtos.PledgeResultDto
import com.project.campaignlift.pledges.dtos.UserPledgeDto
import com.project.campaignlift.pledges.dtos.toResultDto
import com.project.campaignlift.pledges.dtos.toUserPledgeDto
import com.project.campaignlift.repositories.CampaignRepository
import com.project.campaignlift.repositories.PledgeRepository
import com.project.campaignlift.repositories.PledgeTransactionRepository
import com.project.common.exceptions.AccessDeniedException
import com.project.common.exceptions.pledges.InvalidPledgeOperationException
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate


@Service
class PledgeServiceImpl(
    private val pledgeRepository: PledgeRepository,
    private val campaignRepository: CampaignRepository,
    private val pledgeTransactionRepository: PledgeTransactionRepository,
): PledgeService {
    override fun getAllUserPledges(userId: Long): List<UserPledgeDto> {
        return pledgeRepository.findAllByUserIdDto(userId)
    }

    override fun createPledge(
        userId: Long,
        accountId: Long,
        campaignId: Long,
        amount: BigDecimal
    ): PledgeResultDto {
        val campaign = campaignRepository.findById(campaignId)
            .orElseThrow { IllegalArgumentException("Campaign not found") }

        validateCampaignIsPledgeable(campaign)

        val existingPledge = pledgeRepository.findByUserIdAndCampaignId(userId, campaignId)

        val pledge = when {
            existingPledge == null -> {
                PledgeEntity(
                    userId = userId,
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
            existingPledge.status == PledgeStatus.COMMITTED -> {
                throw InvalidPledgeOperationException("You already have a pledge for this campaign.")
            }
            else -> throw InvalidPledgeOperationException("Unhandled pledge status.")
        }

        val savedPledge = pledgeRepository.save(pledge)

        val transaction = PledgeTransactionEntity(
            transactionId = 1L,
            pledge = savedPledge,
            type = PledgeTransactionType.FUNDING
        )

        val savedTransaction = pledgeTransactionRepository.save(transaction)

        return PledgeResultDto(
            pledge = savedPledge.toUserPledgeDto(
                title = campaign.title,
                campaignId = campaign.id!!
            ),
            transaction = savedTransaction.toResultDto()
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
}