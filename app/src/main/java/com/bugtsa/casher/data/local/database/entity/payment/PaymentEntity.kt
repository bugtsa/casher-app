package com.bugtsa.casher.data.local.database.entity.payment

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment")
data class PaymentEntity(val price: String,
                         val date: String,
                         val time: String,
                         val category: String) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

}