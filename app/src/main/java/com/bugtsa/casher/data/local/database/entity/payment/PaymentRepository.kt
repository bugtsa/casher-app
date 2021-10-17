package com.bugtsa.casher.data.local.database.entity.payment

import com.bugtsa.casher.domain.models.PaymentModel
import com.bugtsa.casher.data.network.payment.PaymentPageRes
import io.reactivex.Flowable
import io.reactivex.Single

interface PaymentRepository {

    fun add(payment: PaymentModel): Single<PaymentModel>

    fun saveList(pageRes: PaymentPageRes): Single<PaymentPageRes>

    fun getPaymentsList(): Flowable<List<PaymentModel>>
}