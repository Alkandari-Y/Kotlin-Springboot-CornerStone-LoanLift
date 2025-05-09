package com.project.campaignlift.pledges

import com.project.campaignlift.entities.projections.UserPledgeProjection
import com.project.campaignlift.pledges.dtos.CreatePledgeRequest
import com.project.campaignlift.pledges.dtos.PledgeResultDto
import com.project.campaignlift.pledges.dtos.UpdatePledgeRequest
import com.project.campaignlift.pledges.dtos.UserPledgeDto
import com.project.campaignlift.services.PledgeService
import com.project.common.responses.authenthication.UserInfoDto
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
            userId = authUser.userId,
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

}