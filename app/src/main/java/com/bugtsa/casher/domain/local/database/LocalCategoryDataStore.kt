package com.bugtsa.casher.domain.local.database

import com.bugtsa.casher.data.dto.CategoryDto
import com.bugtsa.casher.domain.local.database.model.CategoryEntity
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class LocalCategoryDataStore @Inject constructor(categoryDao: CategoryDao) :
        LocalCategoryRepository {

    var categoryDao: CategoryDao

    init {
        this.categoryDao = categoryDao
    }

    override fun add(categoryText: String): Single<CategoryDto> {
        return Single.fromCallable<CategoryDto> {
            val rowId = categoryDao.add(CategoryEntity(categoryText))
            CategoryDto(rowId, categoryText)
        }
    }

    override fun getCategoriesList(): Flowable<List<String>> {
        return categoryDao.getCategories()
                .map { it.mapNotNull { it.name } }
    }
}