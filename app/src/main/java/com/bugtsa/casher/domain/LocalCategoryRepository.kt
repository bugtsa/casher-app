package com.bugtsa.casher.domain

import com.bugtsa.casher.data.dto.CategoryDto
import com.bugtsa.casher.model.CategoryEntity
import io.reactivex.Single

interface LocalCategoryRepository {
    fun add(categoryText: String ) : Single<CategoryDto>
}