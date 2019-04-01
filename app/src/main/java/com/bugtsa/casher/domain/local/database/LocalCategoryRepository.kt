package com.bugtsa.casher.domain.local.database

import com.bugtsa.casher.data.dto.*
import com.bugtsa.casher.model.*
import io.reactivex.*

interface LocalCategoryRepository {
    fun add(categoryText: String ) : Single<CategoryDto>
    fun getCategoriesList(): Flowable<List<CategoryEntity>>
}