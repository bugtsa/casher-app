package com.bugtsa.casher.data.local.database.entity.payment

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment")
data class PaymentEntity(
    val id: Int,
    val cost: String,
    val balance: String,
    val date: String,
    val time: String,
    val category: String,
    val categoryId: Int
) {
    @PrimaryKey(autoGenerate = true)
    var localId: Long = 0L
}