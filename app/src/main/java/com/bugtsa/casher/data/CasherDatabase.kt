package com.bugtsa.casher.data

import android.app.Application
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.bugtsa.casher.model.CategoryEntity
import com.bugtsa.casher.model.ModelConstants
import javax.inject.Inject
import javax.inject.Singleton

@Database(entities = arrayOf(CategoryEntity::class), version = 1, exportSchema = false)
abstract class CasherDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
}