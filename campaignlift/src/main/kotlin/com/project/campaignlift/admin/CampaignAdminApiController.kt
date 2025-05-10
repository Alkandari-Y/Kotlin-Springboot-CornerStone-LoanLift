package com.project.campaignlift.admin

import com.project.campaignlift.admin.dtos.CampaignStatusRequest
import com.project.campaignlift.campaigns.dtos.CampaignDetailResponse
import com.project.campaignlift.campaigns.dtos.CampaignListItemResponse
import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.campaignlift.providers.AuthDetailsProvider
import com.project.campaignlift.providers.BankServiceProvider
import com.project.campaignlift.repositories.CategoryRepository
import com.project.campaignlift.services.CampaignService
import com.project.campaignlift.services.MailService
import com.project.campaignlift.services.RepaymentService
import com.project.common.exceptions.auth.MissingCredentialsException
import com.project.common.exceptions.campaigns.CampaignNotFoundException
import com.project.common.exceptions.campaigns.InvalidCampaignStatusChangeException
import com.project.common.exceptions.categories.CategoryNotFoundException
import com.project.common.responses.authenthication.UserInfoDto
import com.project.common.responses.banking.UserAccountsResponse
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/admin/campaigns")
@PreAuthorize("hasRole('ROLE_ADMIN')")
class CampaignAdminApiController(
    private val campaignService: CampaignService,
    private val bankServiceProvider: BankServiceProvider,
    private val repaymentService: RepaymentService,
    private val categoryRepository: CategoryRepository,
    private val authDetailsProvider: AuthDetailsProvider,
    private val mailService: MailService,
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

        return campaign
    }

    @PutMapping("/details/{campaignId}")
    fun editCampaign(campaignId: Long) = "This is a campaign with id: $campaignId"

    @PostMapping("/details/{campaignId}")
    fun approveCampaign(
        @RequestAttribute("authUser") authUser: UserInfoDto,
        @PathVariable campaignId: Long,
        @RequestBody statusRequest: CampaignStatusRequest,
        authentication: Authentication
    ): CampaignEntity {
        val adminToken = authentication.credentials?.toString()
            ?: throw MissingCredentialsException()

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

        val updatedCampaign = campaignService.approveRejectCampaignStatus(
            campaignId = campaignId,
            status = status,
            adminId = approvedBy
        )

        val ownerDetails = authDetailsProvider.getUserDetailsFromAuth(
            adminToken = adminToken,
            userId = updatedCampaign.createdBy!!
        )
        mailService.sendHtmlEmail(
            to = ownerDetails.email,
            subject = "Campaign - ${status.name}",
            bodyText = "Your ${updatedCampaign.title} has been set to ${status.name}",
            username = ownerDetails.username,
        )

        return updatedCampaign
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

        return bankServiceProvider.getUserAccountsAndProfile(owner, adminToken)
    }

    @PostMapping("/demo/run-repayment")
    fun triggerRepayment(): ResponseEntity<String> {
        repaymentService.processMonthlyRepayments()
        return ResponseEntity.ok("Triggered repayment")
    }

    @PostMapping("/demo/run-repayment/{campaignId}")
    fun triggerRepaymentForCampgign(
        @PathVariable("campaignId") campaignId: Long,
    ): ResponseEntity<String> {
        val campaign = campaignService.getCampaignEntityById(campaignId)
            ?: throw CampaignNotFoundException()

        val category = categoryRepository.findByIdOrNull(campaign.categoryId)
            ?: throw CategoryNotFoundException()
        repaymentService.processSingleCampaignRepayment(campaign, mapOf(campaign.categoryId to category))
        return ResponseEntity.ok("Triggered repayment")
    }

}