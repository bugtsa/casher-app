package com.bugtsa.casher.di.inject

import android.app.Application
import androidx.room.Room
import com.bugtsa.casher.data.local.database.CasherDatabase
import com.bugtsa.casher.data.local.database.CasherDatabase.Companion.DB_NAME
import com.bugtsa.casher.data.local.database.entity.category.CategoryDao
import javax.inject.Provider

class CategoryDaoProvider : Provider<CategoryDao> {

    private var categoryDao: CategoryDao

    constructor(application: Application) {
        val database = Room.databaseBuilder(application.applicationContext,
                CasherDatabase::class.java, DB_NAME)
                .allowMainThreadQueries()
                .build()

        categoryDao = database.categoryDao()
    }

    override fun get(): CategoryDao {
        return categoryDao
    }
}