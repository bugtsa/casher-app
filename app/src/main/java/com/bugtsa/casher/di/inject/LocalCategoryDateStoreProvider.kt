package com.bugtsa.casher.di.inject

import com.bugtsa.casher.data.CategoryDao
import com.bugtsa.casher.data.LocalCategoryDataStore
import javax.inject.Provider

class LocalCategoryDateStoreProvider: Provider<LocalCategoryDataStore> {

    var localCategoryDataStore: LocalCategoryDataStore

    constructor(categoryDao: CategoryDao) {
        this.localCategoryDataStore = LocalCategoryDataStore(categoryDao)
    }

    override fun get(): LocalCategoryDataStore {
        return this.localCategoryDataStore
    }
}