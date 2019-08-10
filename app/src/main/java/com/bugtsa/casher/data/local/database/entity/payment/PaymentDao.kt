package com.bugtsa.casher.data.local.database.entity.payment

import androidx.room.*
import com.bugtsa.casher.data.dto.PaymentDto
import io.reactivex.Flowable

@Dao
interface PaymentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(payment: PaymentDto): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(payment: PaymentDto)

    @Query("SELECT * FROM payment")
    fun getPayments(): Flowable<List<PaymentEntity>>
}