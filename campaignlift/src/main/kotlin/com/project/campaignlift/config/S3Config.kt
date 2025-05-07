package com.project.campaignlift.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

@Configuration
class S3Config {

    @Bean
    fun s3Client(): S3Client {
        return S3Client.builder()
            .endpointOverride(URI("http://localhost:9000"))
            .region(Region.US_EAST_1)
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create("minioadmin", "minioadmin")
                )
            )
            .forcePathStyle(true)
            .build()
    }
}
