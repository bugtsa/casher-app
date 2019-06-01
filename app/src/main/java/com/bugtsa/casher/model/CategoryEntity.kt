package com.bugtsa.casher.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class CategoryEntity(var name: String) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}