package com.project.campaignlift.repositories

import com.project.campaignlift.entities.CampaignEntity
import com.project.campaignlift.entities.CampaignStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CampaignRepositories: JpaRepository<CampaignEntity, Long> {
    fun findByStatus(status: CampaignStatus): List<CampaignEntity>
    fun findByCreatedBy(userId: Long): List<CampaignEntity>
}