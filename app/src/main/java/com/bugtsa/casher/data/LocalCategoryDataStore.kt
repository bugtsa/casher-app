package com.bugtsa.casher.data

import com.bugtsa.casher.data.dto.CategoryDto
import com.bugtsa.casher.domain.LocalCategoryRepository
import com.bugtsa.casher.model.CategoryEntity
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class LocalCategoryDataStore @Inject constructor(categoryDao: CategoryDao) : LocalCategoryRepository {

    var categoryDao: CategoryDao

    init {
        this.categoryDao = categoryDao
    }

    override fun add(categoryText: String): Single<CategoryDto> {
        return Single.fromCallable<CategoryDto>{
            val rowId = categoryDao.add(CategoryEntity(categoryText))
            CategoryDto(rowId, categoryText)
        }
    }

    override fun getCategories(): Flowable<List<CategoryEntity>> {
        return categoryDao.getCategories()
    }
}