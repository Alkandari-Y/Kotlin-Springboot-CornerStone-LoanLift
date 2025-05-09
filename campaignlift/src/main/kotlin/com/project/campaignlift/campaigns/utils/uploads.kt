package com.project.campaignlift.campaigns.utils

import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Paths
import java.util.UUID


fun saveMultipartFileLocally(file: MultipartFile): String {
    val uploadDir = Paths.get("uploads").toAbsolutePath().normalize().toFile()
    if (!uploadDir.exists()) uploadDir.mkdirs()

    val filename = UUID.randomUUID().toString() + "_" + file.originalFilename
    val filePath = File(uploadDir, filename)

    file.transferTo(filePath)

    return "/images/$filename"
}
