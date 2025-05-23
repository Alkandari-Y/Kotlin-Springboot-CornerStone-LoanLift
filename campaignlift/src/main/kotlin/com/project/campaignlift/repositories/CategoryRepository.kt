package com.project.campaignlift.repositories

import com.project.banking.entities.CategoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository: JpaRepository<CategoryEntity, Long> {
    fun findAllByIdIn(ids: List<Long>): List<CategoryEntity>

}