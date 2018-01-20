package com.bugtsa.casher.data

import com.bugtsa.casher.data.dto.CategoryDto
import com.bugtsa.casher.domain.LocalCategoryRepository
import com.bugtsa.casher.model.CategoryEntity
import io.reactivex.Single
import org.w3c.dom.Comment
import javax.inject.Inject

class LocalCategoryDataStore @Inject constructor(categoryDao: CategoryDao) : LocalCategoryRepository {

    lateinit var categoryDao: CategoryDao

    init {
        this.categoryDao = categoryDao
    }

    override fun add(categoryText: String): Single<CategoryDto> {
        return Single.fromCallable<CategoryDto>{
            var rowid = categoryDao.add(CategoryEntity(categoryText))
            CategoryDto(rowid, categoryText)
        }
    }
}