package com.bugtsa.casher.data.local.database.entity.payment

import androidx.room.*
import io.reactivex.Flowable

@Dao
interface PaymentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(payment: PaymentEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(payment: PaymentEntity)

    @Query("SELECT * FROM payment WHERE id =:id LIMIT 1")
    fun loadById(id: Int): List<PaymentEntity>

    @Transaction
    fun save(paymentList: List<PaymentEntity>) {
        paymentList.forEach { payment ->
            if (loadById(payment.id).isEmpty()) {
                add(payment)
            } else {
                update(payment)
            }
        }
    }

    @Query("SELECT * FROM payment")
    fun getPayments(): Flowable<List<PaymentEntity>>
}