package com.bugtsa.casher.data.local.database.entity.payment

import com.bugtsa.casher.data.dto.PaymentDto
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class PaymentDataStore @Inject constructor(private val paymentDao: PaymentDao) :
 PaymentRepository{
    override fun add(payment: PaymentDto): Single<PaymentDto> {
         return Single.fromCallable {
             val rowId = paymentDao.add(payment)
             PaymentDto(rowId, payment.price, payment.date, payment.time, payment.category)
         }
    }

    override fun getPaymentsList(): Flowable<List<PaymentDto>> {
        return paymentDao.getPayments()
                .flatMap { list ->
                    when (list.isEmpty()) {
                        true -> Flowable.just(listOf())
                        false -> Flowable.just(list.map { PaymentDto(it) })
                    }
                }
                .firstElement().toFlowable()
    }
}