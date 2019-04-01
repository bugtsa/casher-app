package com.bugtsa.casher.di.inject

import android.app.Application
import android.arch.persistence.room.Room
import com.bugtsa.casher.domain.local.database.CasherDatabase
import com.bugtsa.casher.domain.local.database.CategoryDao
import com.bugtsa.casher.model.ModelConstants
import javax.inject.Provider

class CategoryDaoProvider : Provider<CategoryDao> {

    private var categoryDao: CategoryDao

    constructor(application: Application) {
        val database = Room.databaseBuilder(application.applicationContext,
                CasherDatabase::class.java, ModelConstants.DB_NAME)
                .allowMainThreadQueries()
                .build()

        categoryDao = database.categoryDao()
    }

    override fun get(): CategoryDao {
        return categoryDao
    }
}