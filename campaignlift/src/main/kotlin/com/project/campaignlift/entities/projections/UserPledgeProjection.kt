package com.project.campaignlift.entities.projections

import java.math.BigDecimal
import java.time.LocalDate

interface UserPledgeProjection {
    fun id(): Long
    fun amount(): BigDecimal
    fun status(): String
    fun campaignTitle(): String
    fun campaignId(): Long
    fun createdAt(): LocalDate
}
