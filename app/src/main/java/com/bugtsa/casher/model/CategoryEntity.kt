package com.bugtsa.casher.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "category")
data class CategoryEntity(var name: String) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}