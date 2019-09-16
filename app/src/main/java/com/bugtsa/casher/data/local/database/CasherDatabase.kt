package com.bugtsa.casher.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bugtsa.casher.data.local.database.entity.category.CategoryDao
import com.bugtsa.casher.data.local.database.entity.category.CategoryEntity
import com.bugtsa.casher.data.local.database.entity.payment.PaymentDao
import com.bugtsa.casher.data.local.database.entity.payment.PaymentEntity

@Database(
        entities = [CategoryEntity::class, PaymentEntity::class],
        version = 2, exportSchema = true)

abstract class CasherDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun paymentDao(): PaymentDao

    companion object {
        const val DB_NAME = "casherdb"
    }
}