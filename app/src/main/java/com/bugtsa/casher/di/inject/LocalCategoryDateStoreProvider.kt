package com.bugtsa.casher.di.inject

import com.bugtsa.casher.data.local.database.entity.category.CategoryDao
import com.bugtsa.casher.data.local.database.entity.category.CategoryDataStore
import javax.inject.*

class LocalCategoryDateStoreProvider: Provider<CategoryDataStore> {

    var categoryDataStore: CategoryDataStore

    constructor(categoryDao: CategoryDao) {
        this.categoryDataStore =
                CategoryDataStore(categoryDao)
    }

    override fun get(): CategoryDataStore {
        return this.categoryDataStore
    }
}