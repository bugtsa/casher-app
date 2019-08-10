package com.bugtsa.casher.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bugtsa.casher.data.local.database.entity.category.CategoryDao
import com.bugtsa.casher.data.local.database.entity.category.CategoryEntity

@Database(entities = arrayOf(CategoryEntity::class), version = 1, exportSchema = true)
abstract class CasherDatabase : RoomDatabase() {
    companion object {
        val DB_NAME = "casherdb"
    }

    abstract fun categoryDao(): CategoryDao
}