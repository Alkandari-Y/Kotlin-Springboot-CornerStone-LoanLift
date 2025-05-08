package com.project.campaignlift.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.core.sync.RequestBody
import java.util.*

@Service
class FileStorageService(private val s3Client: S3Client) {

    private val bucketName = "loanlift-public"

    fun uploadFilePublic(file: MultipartFile): String {
        val key = UUID.randomUUID().toString()
        val bucketName = "loanlift-public"
        val request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(file.contentType)
            .build()

        s3Client.putObject(request, RequestBody.fromBytes(file.bytes))

        return "http://localhost:9000/$bucketName/$key"
    }
}
