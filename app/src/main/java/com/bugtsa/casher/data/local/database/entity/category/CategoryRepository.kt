package com.bugtsa.casher.data.local.database.entity.category

import com.bugtsa.casher.data.dto.CategoryDto
import io.reactivex.Flowable
import io.reactivex.Single

interface CategoryRepository {
    fun add(category: CategoryDto): Single<CategoryDto>
    fun getCategoriesList(): Flowable<List<CategoryDto>>
}