package com.project.campaignlift.repositories

import com.project.campaignlift.entities.FileEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FileRepository: JpaRepository<FileEntity, Long> {
}