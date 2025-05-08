package com.project.campaignlift.admin

import com.project.campaignlift.admin.dtos.CampaignStatusRequest
import com.project.campaignlift.campaigns.dtos.CampaignDetailResponse
import com.project.campaignlift.campaigns.dtos.CampaignListItemResponse
import com.project.campaignlift.campaigns.dtos.toDetailResponse
import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.campaignlift.providers.BandServiceProvider
import com.project.campaignlift.repositories.CommentRepository
import com.project.campaignlift.services.CampaignService
import com.project.common.exceptions.APIException
import com.project.common.exceptions.ErrorCode
import com.project.common.responses.authenthication.UserInfoDto
import com.project.common.responses.banking.UserAccountsResponse
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/admin/campaigns")
@PreAuthorize("hasRole('ROLE_ADMIN')")
class CampaignAdminApiController (
    private val campaignService: CampaignService,
    private val bandServiceProvider: BandServiceProvider,
){

    @GetMapping("/list")
    fun getAllCampaigns(
        @RequestParam(required = false) status: CampaignStatus?
    ): List<CampaignListItemResponse> {
        if (status != null) {
            return campaignService.getAllCampaignsByStatus(status)
        }
        return campaignService.getAllCampaigns()
    }

    @GetMapping("/details/{campaignId}")
    fun getCampaignById(@PathVariable("campaignId") campaignId: Long): CampaignDetailResponse {
        val campaign = campaignService.getCampaignById(campaignId)
        ?: throw APIException(
                "Campaign with id $campaignId not found",
                HttpStatus.NOT_FOUND,
                ErrorCode.ACCOUNT_NOT_FOUND
            )
        return campaign.toDetailResponse()
    }

    @PutMapping("/details/{campaignId}")
    fun editCampaign(campaignId: Long) = "This is a campaign with id: $campaignId"

    @PostMapping("/details/{campaignId}")
    fun approveCampaign(
        @RequestAttribute("authUser") authUser: UserInfoDto,
        @PathVariable campaignId: Long,
        @RequestBody statusRequest: CampaignStatusRequest
    ): CampaignEntity {
        val status = statusRequest.name

        val allowedStatuses = setOf(
            CampaignStatus.PENDING,
            CampaignStatus.REJECTED,
            CampaignStatus.ACTIVE
        )

        if (status !in allowedStatuses) {
            throw APIException("Invalid status change: $status", HttpStatus.BAD_REQUEST, ErrorCode.INVALID_INPUT)
        }

        val approvedBy = if (status == CampaignStatus.PENDING) null else authUser.userId

        return campaignService.approveRejectCampaignStatus(
            campaignId = campaignId,
            status = status,
            adminId = approvedBy
        )
    }


    @GetMapping("/details/{campaignId}/owner")
    fun getCampaignOwnerDetails(
        @PathVariable("campaignId") campaignId: Long,
        authentication: Authentication
    ): UserAccountsResponse {
        val campaign = campaignService.getCampaignById(campaignId)
            ?: throw APIException(
                "Campaign with id $campaignId not found",
                HttpStatus.NOT_FOUND,
                ErrorCode.ACCOUNT_NOT_FOUND
            )
        val adminToken = authentication.credentials?.toString()

        if (adminToken.isNullOrEmpty()) {
            throw APIException(
                "Invalid Request. Please provide admin credentials",
                HttpStatus.UNAUTHORIZED,
                ErrorCode.INVALID_CREDENTIALS
            )
        }

        val owner = campaign.createdBy ?: throw APIException("Campaign owner not found", HttpStatus.NOT_FOUND, ErrorCode.ACCOUNT_NOT_FOUND)

        return bandServiceProvider.getUserAccountsAndProfile(owner, adminToken)

    }
}