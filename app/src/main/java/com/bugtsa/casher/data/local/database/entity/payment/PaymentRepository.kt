package com.bugtsa.casher.data.local.database.entity.payment

import com.bugtsa.casher.data.dto.PaymentDto
import com.bugtsa.casher.data.network.payment.PaymentPageRes
import io.reactivex.Flowable
import io.reactivex.Single

interface PaymentRepository {

    fun add(payment: PaymentDto): Single<PaymentDto>

    fun saveList(pageRes: PaymentPageRes): Single<PaymentPageRes>

    fun getPaymentsList(): Flowable<List<PaymentDto>>
}