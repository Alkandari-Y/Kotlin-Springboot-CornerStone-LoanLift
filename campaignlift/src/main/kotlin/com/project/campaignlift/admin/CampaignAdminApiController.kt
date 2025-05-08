package com.project.campaignlift.admin

import com.project.campaignlift.admin.dtos.CampaignStatusRequest
import com.project.campaignlift.campaigns.dtos.CampaignDetailResponse
import com.project.campaignlift.campaigns.dtos.CampaignListItemResponse
import com.project.campaignlift.campaigns.dtos.toDetailResponse
import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.campaignlift.providers.BandServiceProvider
import com.project.campaignlift.services.CampaignService
import com.project.common.exceptions.auth.MissingCredentialsException
import com.project.common.exceptions.campaigns.CampaignNotFoundException
import com.project.common.exceptions.campaigns.CampaignOwnerNotFoundException
import com.project.common.exceptions.campaigns.InvalidCampaignStatusChangeException
import com.project.common.responses.authenthication.UserInfoDto
import com.project.common.responses.banking.UserAccountsResponse
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
    fun getCampaignById(@PathVariable("campaignId") campaignId: Long)
    : CampaignDetailResponse {
        val campaign = campaignService.getCampaignById(campaignId)
            ?: throw CampaignNotFoundException()

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
            throw InvalidCampaignStatusChangeException(status.name)
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
            ?: throw CampaignNotFoundException()

        val adminToken = authentication.credentials?.toString()
            ?: throw MissingCredentialsException()

        val owner = campaign.createdBy
            ?: throw CampaignOwnerNotFoundException(campaignId)

        return bandServiceProvider.getUserAccountsAndProfile(owner, adminToken)
    }
}