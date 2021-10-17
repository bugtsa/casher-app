package com.bugtsa.casher.data.local.database.entity.payment

import com.bugtsa.casher.domain.models.PaymentModel
import com.bugtsa.casher.domain.models.PaymentModel.Companion.INT_EMPTY_PAYMENT_FIELD
import com.bugtsa.casher.data.network.payment.PaymentPageRes
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class PaymentDataStore @Inject constructor(private val paymentDao: PaymentDao) :
    PaymentRepository {

    override fun add(payment: PaymentModel): Single<PaymentModel> {
        return Single.fromCallable {
            paymentDao.add(
                PaymentEntity(
                    id = payment.id,
                    cost = payment.cost,
                    category = payment.category,
                    categoryId = payment.categoryId,
                    date = payment.date,
                    time = payment.time,
                    balance = payment.balance
                )
            )
            payment
        }
    }

    override fun saveList(
        pageRes: PaymentPageRes
    ): Single<PaymentPageRes> {
        return Single.fromCallable {
            val paymentList = pageRes.page.map { it.payment ?: PaymentModel.paymentEmptyDto() }
            val entityList = paymentList
                .filter { it.id != INT_EMPTY_PAYMENT_FIELD }
                .map { payment ->
                    PaymentEntity(
                        id = payment.id,
                        cost = payment.cost,
                        category = payment.category,
                        categoryId = payment.categoryId,
                        date = payment.date,
                        time = payment.time,
                        balance = payment.balance
                    )
                }
            paymentDao.save(entityList)
            pageRes
        }
    }

    override fun getPaymentsList(): Flowable<List<PaymentModel>> {
        return paymentDao.getPayments()
            .flatMap { list ->
                when (list.isEmpty()) {
                    true -> Flowable.just(listOf())
                    false -> Flowable.just(list.map { PaymentModel(it) })
                }
            }
            .firstElement().toFlowable()
    }
}