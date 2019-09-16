package com.bugtsa.casher.di.inject

import android.app.Application
import androidx.room.Room
import com.bugtsa.casher.data.local.database.CasherDatabase
import javax.inject.Provider

class DataBaseProvider(val application: Application): Provider<CasherDatabase> {

    override fun get(): CasherDatabase {
        return Room.databaseBuilder(application.applicationContext,
                CasherDatabase::class.java, CasherDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build()
    }
}