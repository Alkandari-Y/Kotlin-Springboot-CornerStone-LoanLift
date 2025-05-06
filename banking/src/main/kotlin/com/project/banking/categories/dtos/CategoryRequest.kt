package com.project.banking.categories.dtos

import com.project.banking.entities.CategoryEntity
import jakarta.validation.constraints.NotBlank

data class CategoryRequest(
    @field:NotBlank
    val name: String
)

fun CategoryRequest.toEntity() = CategoryEntity(name = name)
