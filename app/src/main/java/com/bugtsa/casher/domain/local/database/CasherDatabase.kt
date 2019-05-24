package com.bugtsa.casher.domain.local.database

import androidx.room.*
import com.bugtsa.casher.model.*

@Database(entities = arrayOf(CategoryEntity::class), version = 1, exportSchema = false)
abstract class CasherDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
}