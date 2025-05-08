package com.project.campaignlift.campaigns

import com.project.campaignlift.campaigns.dtos.CampaignListItemResponse
import com.project.campaignlift.campaigns.dtos.CampaignWithCommentsDto
import com.project.campaignlift.campaigns.dtos.CreateCampaignDto
import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.campaignlift.providers.BandServiceProvider
import com.project.campaignlift.services.CampaignService
import com.project.common.exceptions.auth.MissingCredentialsException
import com.project.common.exceptions.campaigns.CampaignNotFoundException
import com.project.common.exceptions.kycs.AccountNotVerifiedException
import com.project.common.responses.authenthication.UserInfoDto
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/campaigns")
class CampaignApiController (
    private val campaignService: CampaignService,
    private val bandServiceProvider: BandServiceProvider
) {
    @GetMapping
    fun getAllCampaigns(): ResponseEntity<List<CampaignListItemResponse>> =
        ResponseEntity(
            campaignService.getAllCampaignsByStatus(CampaignStatus.ACTIVE),
            HttpStatus.OK
        )


    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(consumes = ["multipart/form-data"])
    fun createCampaign(
        @Valid @ModelAttribute campaignCreateRequest: CreateCampaignDto,
        @RequestPart("image", required = true) image: MultipartFile,
        @RequestAttribute("authUser") authUser: UserInfoDto,
        authentication: Authentication
    ): ResponseEntity<CampaignEntity> {

        if (authUser.isActive.not()) {
            throw AccountNotVerifiedException()
        }

        val token = authentication.credentials?.toString()

        if (token.isNullOrEmpty()) {
            throw MissingCredentialsException()
        }
        val account = bandServiceProvider.getAccount(
            campaignCreateRequest.accountId,
            token,
            authUser.userId
        )
        val campaign = campaignService.createCampaign(
            campaignDto = campaignCreateRequest,
            user = authUser,
            image = image,
            accountId = account.id
        )
        return ResponseEntity(campaign, HttpStatus.CREATED)
    }

    @GetMapping("/details/{campaignId}")
    fun getCampaignById(
        @PathVariable("campaignId") campaignId: Long): CampaignWithCommentsDto
    {
        val campaign = campaignService.getCampaignDetails(campaignId)
        ?: throw CampaignNotFoundException()

        if (campaign.status in listOf(CampaignStatus.NEW, CampaignStatus.REJECTED, CampaignStatus.PENDING)) {
            throw  CampaignNotFoundException()
        }
        return campaign
    }

    @GetMapping("/manage")
    fun getMyCampaigns(
        @RequestAttribute("authUser") authUser: UserInfoDto,
    ): List<CampaignListItemResponse> {
        return campaignService.getAllByUserId(authUser.userId)
    }


    @DeleteMapping("/manage/{campaignId}")
    fun deleteCampaign(
        @PathVariable campaignId: Long,
        @RequestAttribute("authUser") authUser: UserInfoDto,
    ): ResponseEntity<Unit> {
        campaignService.deleteCampaign(campaignId, authUser.userId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}