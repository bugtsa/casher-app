package com.bugtsa.casher.di.inject.category

import com.bugtsa.casher.data.local.database.entity.category.CategoryDao
import com.bugtsa.casher.data.local.database.entity.category.CategoryDataStore
import javax.inject.Provider

class LocalCategoryDateStoreProvider(categoryDao: CategoryDao): Provider<CategoryDataStore> {

    private val categoryDataStore: CategoryDataStore = CategoryDataStore(categoryDao)

    override fun get(): CategoryDataStore {
        return this.categoryDataStore
    }
}