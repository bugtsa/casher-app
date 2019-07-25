package com.bugtsa.casher.domain.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bugtsa.casher.domain.local.database.model.CategoryEntity

@Database(entities = arrayOf(CategoryEntity::class), version = 1, exportSchema = true)
abstract class CasherDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
}