package com.project.campaignlift.services

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.*

@Service
class FileStorageService(private val s3Client: S3Client) {

    private val bucketName = "loanlift-public"

    fun uploadFile(file: MultipartFile): String {
        val key = UUID.randomUUID().toString()

        val request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(file.contentType)
            .build()

        s3Client.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.bytes))

        return "http://localhost:9000/$bucketName/$key"
    }
}
