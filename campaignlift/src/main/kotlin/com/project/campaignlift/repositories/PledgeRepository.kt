package com.project.campaignlift.repositories

import com.project.campaignlift.entities.PledgeEntity
import com.project.campaignlift.entities.PledgeStatus
import com.project.campaignlift.entities.projections.UserPledgeProjection
import com.project.campaignlift.pledges.dtos.UserPledgeDto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PledgeRepository: JpaRepository<PledgeEntity, Long> {

    @Query("""
        SELECT 
            p.id AS id,
            p.amount AS amount,
            p.status AS status,
            c.title AS campaignTitle,
            c.id AS campaignId,
            p.createdAt AS createdAt
        FROM PledgeEntity p
        JOIN p.campaign c
        WHERE p.userId = :userId
    """)
    fun findAllByUserIdProjected(userId: Long): List<UserPledgeProjection>

    @Query("""
    SELECT new com.project.campaignlift.pledges.dtos.UserPledgeDto(
        p.id,
        p.amount,
        p.status,
        c.title,
        c.id,
        p.createdAt
    )
    FROM PledgeEntity p
    JOIN p.campaign c
    WHERE p.userId = :userId
""")
    fun findAllByUserIdDto(userId: Long): List<UserPledgeDto>


    fun findByUserIdAndCampaignId(userId: Long, campaignId: Long): PledgeEntity?

    fun findByUserIdAndCampaignIdAndStatus(userId: Long, campaignId: Long, status: PledgeStatus): PledgeEntity?
    fun existsByUserIdAndCampaignIdAndStatus(userId: Long, campaignId: Long, status: PledgeStatus): Boolean

}