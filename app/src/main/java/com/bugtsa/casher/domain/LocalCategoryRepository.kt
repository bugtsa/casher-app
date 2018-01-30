package com.bugtsa.casher.domain

import com.bugtsa.casher.data.dto.CategoryDto
import com.bugtsa.casher.model.CategoryEntity
import io.reactivex.Flowable
import io.reactivex.Single
import org.w3c.dom.Comment

interface LocalCategoryRepository {
    fun add(categoryText: String ) : Single<CategoryDto>
    fun getCategories(): Flowable<List<CategoryEntity>>
}