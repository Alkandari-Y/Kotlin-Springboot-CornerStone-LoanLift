package com.project.campaignlift.pledges

import com.project.campaignlift.pledges.dtos.*
import com.project.campaignlift.services.CampaignService
import com.project.campaignlift.services.PledgeService
import com.project.common.responses.authenthication.UserInfoDto
import com.project.common.security.RemoteUserPrincipal
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/pledges")
class PledgeApiController(
    private val pledgeService: PledgeService,
    private val campaignService: CampaignService,
) {

    @GetMapping
    fun getAllMyPledges(
            @RequestAttribute("authUser") authUser: UserInfoDto,
        ): List<UserPledgeDto> {
        return pledgeService.getAllUserPledges(authUser.userId)
    }

    @PostMapping
    fun createPledge(
        @Valid @RequestBody request: CreatePledgeRequest,
        @RequestAttribute("authUser") authUser: UserInfoDto
    ): ResponseEntity<PledgeResultDto> {
        val pledge = pledgeService.createPledge(
            userInfo = authUser,
            accountId = request.accountId!!,
            campaignId = request.campaignId!!,
            amount = request.amount!!
        )

        return ResponseEntity(pledge, HttpStatus.CREATED)
    }

    @PutMapping("/details/{pledgeId}")
    fun updatePledge(
        @PathVariable pledgeId: Long,
        @Valid @RequestBody request: UpdatePledgeRequest,
        @RequestAttribute("authUser") authUser: UserInfoDto
    ): ResponseEntity<PledgeResultDto> {
        val pledge = pledgeService.updatePledge(
            pledgeId = pledgeId,
            userId = authUser.userId,
            newAmount = request.amount!!
        )
        return ResponseEntity(pledge, HttpStatus.OK)
    }

    @GetMapping("/details/{pledgeId}")
    fun getPledgeDetails(
        @PathVariable pledgeId: Long,
        @RequestAttribute("authUser") authUser: UserInfoDto,
        @AuthenticationPrincipal principal: RemoteUserPrincipal
    ): ResponseEntity<UserPledgeDto> {
        val pledge = pledgeService.getPledgeDetails(pledgeId)

        val isOwner = authUser.userId == pledge.userId
        val isAdmin = principal.authorities.any { it.authority == "ROLE_ADMIN" }

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val campaign = pledge.campaign

        return ResponseEntity(pledge.toUserPledgeDto(
            title = campaign.title,
            campaign = campaign,
        ), HttpStatus.OK)
    }

    @GetMapping("/details/{pledgeId}/transactions")
    fun getPledgeDetailedTransactions(
        @PathVariable pledgeId: Long,
        @RequestAttribute("authUser") authUser: UserInfoDto,
        @AuthenticationPrincipal principal: RemoteUserPrincipal
    ): ResponseEntity<List<PledgeTransactionWithDetails>> {
        val pledge = pledgeService.getPledgeDetails(pledgeId)

        val isOwner = authUser.userId == pledge.userId
        val isAdmin = principal.authorities.any { it.authority == "ROLE_ADMIN" }

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val pledgeTransactions = pledgeService.getPledgeTransactions(pledgeId)

        return ResponseEntity(pledgeTransactions, HttpStatus.OK)
    }



    @DeleteMapping("/details/{pledgeId}")
    fun withdrawPledge(
        @PathVariable pledgeId: Long,
        @RequestAttribute("authUser") authUser: UserInfoDto
    ): ResponseEntity<Unit> {
        pledgeService.withdrawPledge(pledgeId, authUser.userId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

}