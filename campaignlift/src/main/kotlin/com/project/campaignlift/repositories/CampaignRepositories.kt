package com.project.campaignlift.repositories

import com.project.campaignlift.entities.CampaignEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CampaignRepositories: JpaRepository<CampaignEntity, Long> {

}