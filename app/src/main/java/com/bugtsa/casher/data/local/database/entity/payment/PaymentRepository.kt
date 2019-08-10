package com.bugtsa.casher.data.local.database.entity.payment

import com.bugtsa.casher.data.dto.PaymentDto
import io.reactivex.Flowable
import io.reactivex.Single

interface PaymentRepository {
    fun add(payment: PaymentDto): Single<PaymentDto>
    fun getPaymentsList(): Flowable<List<PaymentDto>>
}