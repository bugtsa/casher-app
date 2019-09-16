package com.bugtsa.casher.data.local.database.entity.payment

import androidx.room.*
import io.reactivex.Flowable

@Dao
interface PaymentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(payment: PaymentEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(payment: PaymentEntity)

    @Query("SELECT * FROM payment")
    fun getPayments(): Flowable<List<PaymentEntity>>
}