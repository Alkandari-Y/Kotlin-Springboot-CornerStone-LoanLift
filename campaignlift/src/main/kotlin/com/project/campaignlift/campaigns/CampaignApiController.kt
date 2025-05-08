package com.project.campaignlift.campaigns

import com.project.campaignlift.campaigns.dtos.CreateCampaignDto
import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.campaignlift.providers.BandServiceProvider
import com.project.campaignlift.services.CampaignService
import com.project.common.exceptions.APIException
import com.project.common.exceptions.ErrorCode
import com.project.common.responses.authenthication.UserInfoDto
import jakarta.validation.Valid
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.MediaTypeFactory
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
import java.nio.file.Paths

@RestController
@RequestMapping("/api/v1/campaigns")
class CampaignApiController (
    private val campaignService: CampaignService,
    private val bandServiceProvider: BandServiceProvider
) {
    @GetMapping
    fun getAllCampaigns(): ResponseEntity<List<CampaignEntity>> =
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
            throw APIException("kyc not verified")
        }

        val token = authentication.credentials?.toString()

        if (token.isNullOrEmpty()) {
            throw APIException(
                "Invalid Request. Please provide admin credentials",
                HttpStatus.UNAUTHORIZED,
                ErrorCode.INVALID_CREDENTIALS
            )
        }
        val account = bandServiceProvider.getAccount(campaignCreateRequest.accountId, token)

        val campaign = campaignService.createCampaign(
            campaignDto = campaignCreateRequest,
            user = authUser,
            image = image,
            accountId = account.id
        )
        return ResponseEntity(campaign, HttpStatus.CREATED)
    }

    @GetMapping("/details/{campaignId}")
    fun getPublicFile(
        @PathVariable("campaignId") campaignId: Long): CampaignEntity
    {
        val campaign = campaignService.getCampaignById(campaignId)
        ?: throw APIException(
                "Campaign with id $campaignId not found",
                HttpStatus.NOT_FOUND,
                ErrorCode.ACCOUNT_NOT_FOUND
        )
        if (campaign.status in listOf(CampaignStatus.NEW, CampaignStatus.REJECTED, CampaignStatus.PENDING)) {
            throw  APIException(
                "Campaign with id $campaignId not found",
                HttpStatus.NOT_FOUND,
                ErrorCode.ACCOUNT_NOT_FOUND
            )
        }
        return campaign
    }

    @GetMapping("/manage")
    fun getMyCampaigns(
        @RequestAttribute("authUser") authUser: UserInfoDto,
    ): List<CampaignEntity> {
        return campaignService.getALlByUserId(authUser.userId)
    }


    @DeleteMapping("/manage/{campaignId}")
    fun deleteCampaign(
        @PathVariable campaignId: Long,
        @RequestAttribute("authUser") authUser: UserInfoDto,
    ): ResponseEntity<Unit> {

        val campaign = campaignService.getCampaignById(campaignId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        if (campaign.createdBy != authUser.userId)
            return ResponseEntity(HttpStatus.FORBIDDEN)
        if (campaign.status != CampaignStatus.NEW) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        campaignService.deleteCampaign(campaignId)
        return ResponseEntity(HttpStatus.OK)
    }
}