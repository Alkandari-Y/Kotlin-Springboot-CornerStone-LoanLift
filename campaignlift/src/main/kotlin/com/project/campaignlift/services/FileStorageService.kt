package com.project.campaignlift.services

import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.FileEntity
import com.project.campaignlift.repositories.FileRepository
import com.project.common.enums.ErrorCode
import com.project.common.exceptions.APIException
import com.project.common.responses.authenthication.UserInfoDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.util.*
import java.time.Duration

@Service
class FileStorageService(
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner,
    private val fileRepository: FileRepository,
    @Value("\${aws.bucket.public}") private val publicBucket: String,
    @Value("\${aws.bucket.private}") private val privateBucket: String
) {
    val allowedTypes = setOf(
        "application/pdf",
        "image/jpeg",
        "image/png",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",       // .xlsx
        "text/plain"
    )

    fun findFile(fileId: Long): FileEntity? {
        return fileRepository.findByIdOrNull(fileId)
    }

    fun submitFileForCampaign(
        file: MultipartFile,
        campaign: CampaignEntity,
        user: UserInfoDto,
        isPublic: Boolean
    ): FileEntity {
        val contentType = file.contentType
            ?: throw APIException(
                message = "Unknown content type",
                httpStatus = HttpStatus.BAD_REQUEST,
                code = ErrorCode.INVALID_INPUT
            )

        if (contentType !in allowedTypes) {
            throw APIException(
                message = "File type $contentType is not supported.",
                httpStatus = HttpStatus.BAD_REQUEST,
                code = ErrorCode.INVALID_INPUT
            )
        }

        // Max file size 10 MB
        val maxFileSizeBytes = 10 * 1024 * 1024
        if (file.size > maxFileSizeBytes) {
            throw APIException(
                message = "File is too large. Max size is 10MB.",
                httpStatus = HttpStatus.BAD_REQUEST,
                code = ErrorCode.INVALID_INPUT
            )
        }

        val (bucket, url) = uploadFile(file, isPublic)

        val fileEntity = FileEntity(
            campaign = campaign,
            mediaType = file.contentType ?: "application/octet-stream",
            url = url,
            bucket = bucket,
            isPublic = isPublic,
            verifiedBy = null
        )

        return fileRepository.save(fileEntity)
    }

    fun uploadFile(file: MultipartFile, isPublic: Boolean): Pair<String, String> {
        val bucket = if (isPublic) publicBucket else privateBucket
        val key = UUID.randomUUID().toString()

        val putRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(file.contentType)
            .build()

        s3Client.putObject(putRequest, RequestBody.fromBytes(file.bytes))

        return Pair(bucket, key)
    }


    fun generatePreSignedUrl(bucket: String, key: String, minutes: Long = 15): String {
        val getRequest = GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build()

        val presignRequest = GetObjectPresignRequest.builder()
            .getObjectRequest(getRequest)
            .signatureDuration(Duration.ofMinutes(minutes))
            .build()

        return s3Presigner.presignGetObject(presignRequest).url().toString()
    }
}
