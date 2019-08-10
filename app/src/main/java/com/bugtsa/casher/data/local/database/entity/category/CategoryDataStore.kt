package com.bugtsa.casher.data.local.database.entity.category

import com.bugtsa.casher.data.dto.CategoryDto
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class CategoryDataStore @Inject constructor(private val categoryDao: CategoryDao) :
        CategoryRepository {

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