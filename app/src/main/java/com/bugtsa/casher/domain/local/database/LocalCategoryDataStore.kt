package com.bugtsa.casher.domain.local.database

import com.bugtsa.casher.data.dto.CategoryDto
import com.bugtsa.casher.domain.local.database.model.CategoryEntity
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class LocalCategoryDataStore @Inject constructor(categoryDao: CategoryDao) :
        LocalCategoryRepository {

    private val categoryDao: CategoryDao = categoryDao

    override fun add(category: CategoryDto): Single<CategoryDto> {
        return Single.fromCallable {
            val rowId = categoryDao.add(CategoryEntity(category.id, category.name))
            CategoryDto(rowId, category.name)
        }
    }

    override fun getCategoriesList(): Flowable<List<CategoryDto>> {
        return categoryDao.getCategories()
                .flatMap { list ->
                    when (list.isEmpty()) {
                        true -> Flowable.just(listOf())
                        false -> Flowable.just(list.map { CategoryDto(it) })
                    }
                }
                .firstElement().toFlowable()
    }
}