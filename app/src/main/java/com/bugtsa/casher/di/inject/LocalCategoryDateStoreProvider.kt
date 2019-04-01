package com.bugtsa.casher.di.inject

import com.bugtsa.casher.domain.local.database.*
import javax.inject.*

class LocalCategoryDateStoreProvider: Provider<LocalCategoryDataStore> {

    var localCategoryDataStore: LocalCategoryDataStore

    constructor(categoryDao: CategoryDao) {
        this.localCategoryDataStore =
            LocalCategoryDataStore(categoryDao)
    }

    override fun get(): LocalCategoryDataStore {
        return this.localCategoryDataStore
    }
}