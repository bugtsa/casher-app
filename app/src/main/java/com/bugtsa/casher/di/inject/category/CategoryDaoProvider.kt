package com.bugtsa.casher.di.inject.category

import com.bugtsa.casher.data.local.database.CasherDatabase
import com.bugtsa.casher.data.local.database.entity.category.CategoryDao
import javax.inject.Provider

class CategoryDaoProvider(private val casherDatabase: CasherDatabase) : Provider<CategoryDao> {

    override fun get(): CategoryDao {
        return casherDatabase.categoryDao()
    }
}