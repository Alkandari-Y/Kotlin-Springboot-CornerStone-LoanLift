package com.project.campaignlift.campaigns

import com.project.campaignlift.campaigns.dtos.*
import com.project.campaignlift.campaigns.dtos.CampaignListItemResponse
import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import com.project.campaignlift.services.CampaignService
import com.project.campaignlift.services.FileStorageService
import com.project.common.exceptions.auth.MissingCredentialsException
import com.project.common.exceptions.campaigns.CampaignNotFoundException
import com.project.common.exceptions.campaigns.CampaignPermissionDeniedException
import com.project.common.exceptions.campaigns.CampaignUpdateNotAllowedException
import com.project.common.exceptions.kycs.AccountNotVerifiedException
import com.project.common.responses.authenthication.UserInfoDto
import com.project.common.security.RemoteUserPrincipal
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/campaigns")
class CampaignApiController (
    private val campaignService: CampaignService,
    private val fileStorageService: FileStorageService,
    @Value("\${aws.endpoint}")
    val endpoint: String
) {
    @GetMapping
    fun getAllCampaigns(): ResponseEntity<List<CampaignListItemResponse>> =
        ResponseEntity(
            campaignService.getAllApprovedCampaigns(),
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

        val allowedTypes = listOf("image/jpeg", "image/png", "image/jpg", "image/webp")
        if (image.contentType !in allowedTypes) {
            throw IllegalArgumentException("Only JPEG, PNG, or WEBP images are allowed.")
        }

        val maxSizeInBytes = 2 * 1024 * 1024 // 2 MB
        if (image.size > maxSizeInBytes) {
            throw IllegalArgumentException("File size must not exceed 2MB.")
        }

        val campaign = campaignService.createCampaign(
            campaignDto = campaignCreateRequest,
            user = authUser,
            image = image,
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
        campaignService.deleteCampaign(campaignId, authUser)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PutMapping("/manage/{campaignId}",
        consumes = ["multipart/form-data"]
    )
    fun updateCampaign(
        @PathVariable campaignId: Long,
        @Valid @ModelAttribute campaignUpdateRequest: UpdateCampaignRequest,
        @RequestPart("image", required = false) image: MultipartFile?,
        @RequestAttribute("authUser") authUser: UserInfoDto,
    ): ResponseEntity<CampaignEntity> {
        val updated = campaignService.updateCampaign(
            campaignId = campaignId,
            user = authUser,
            campaign = campaignUpdateRequest,
            image = image
        )
        return ResponseEntity(updated, HttpStatus.OK)
    }


    @PostMapping("/manage/files")
    fun uploadFile(
        @Valid @ModelAttribute fileUploadRequest: FileUploadRequest,
        @RequestPart("file", required = true) file: MultipartFile,
        @RequestAttribute("authUser") authUser: UserInfoDto,
    ): ResponseEntity<FileDto> {

        val campaign: CampaignEntity = campaignService.getCampaignEntityById(
            fileUploadRequest.campaignId
        ) ?: throw CampaignNotFoundException()

        if (campaign.status !in listOf(CampaignStatus.NEW, CampaignStatus.PENDING)) {
            throw CampaignUpdateNotAllowedException(campaign.status.name)
        }

        if (campaign.createdBy != authUser.userId) {
            throw CampaignPermissionDeniedException(authUser.userId, campaign.id!!)
        }

        val uploadedFile = fileStorageService.submitFileForCampaign(
            file,
            campaign,
            authUser,
            fileUploadRequest.isPublic
        ).toDto()

        return ResponseEntity(uploadedFile, HttpStatus.CREATED)
    }

    @GetMapping("/manage/files/{fileId}/download")
    fun downloadFile(
        @PathVariable fileId: Long,
        @AuthenticationPrincipal user: RemoteUserPrincipal,
        @RequestAttribute("authUser") authUser: UserInfoDto,
    ): ResponseEntity<DownloadDto> {
        val file = fileStorageService.findFile(fileId) ?: return ResponseEntity.notFound().build()

        if (!file.isPublic) {
            val isOwner = file.campaign?.createdBy == authUser.userId
            val isAdmin = user.authorities.any { it.authority == "ROLE_ADMIN" }

            if (!isOwner && !isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
            }

            val url = fileStorageService.generatePreSignedUrl(file.bucket, file.url)
            return ResponseEntity.ok(
                DownloadDto(url = url)
            )
        }

        return ResponseEntity.ok(
            DownloadDto(url = "$endpoint/${file.bucket}/${file.url}")
        )
    }
}