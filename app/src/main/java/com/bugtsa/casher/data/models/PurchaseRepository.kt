package com.bugtsa.casher.data.models

import com.bugtsa.casher.data.dto.CategoryDto
import com.bugtsa.casher.data.dto.PaymentDto
import com.bugtsa.casher.data.network.payment.PaymentPageRes
import com.bugtsa.casher.data.network.payment.PaymentsByDayRes
import com.bugtsa.casher.global.extentions.Backoff
import com.bugtsa.casher.global.extentions.exponentialRetry
import com.bugtsa.casher.networking.CasherApi
import io.reactivex.Flowable
import io.reactivex.Single
import okhttp3.FormBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PurchaseRepository @Inject constructor(private val casherRestApi: CasherApi) {

    init {
        instance = this
    }

    fun getCategoriesList(): Flowable<List<CategoryDto>> {
        return casherRestApi.getCategories()
                .exponentialRetry(CATEGORY_TIMEOUT, Backoff(maxDelay = CATEGORY_TIMEOUT))
                .timeout(CATEGORY_TIMEOUT, TimeUnit.MILLISECONDS, Flowable.error(Throwable()))
                .map { list ->
                    list.filter { res -> res.id != null && res.name.isNullOrEmpty().not() }
                            .map { res -> CategoryDto(res.id?.toLong() ?: 0L, res.name ?: "") }
                }
    }

    fun getPaymentsByDay(authHeader: String): Flowable<PaymentPageRes> =
            casherRestApi.getPagedPayments(authHeader)
                    .exponentialRetry(PAYMENT_TIMEOUT, Backoff(maxDelay = PAYMENT_TIMEOUT))
                    .timeout(PAYMENT_TIMEOUT, TimeUnit.MILLISECONDS, Flowable.error(Throwable()))

    fun addPayment(payment: FormBody): Single<PaymentDto> =
            casherRestApi.addPayment(payment)
                    .exponentialRetry(ADD_DATA_TIMEOUT, Backoff(maxDelay = ADD_DATA_TIMEOUT))
                    .timeout(ADD_DATA_TIMEOUT, TimeUnit.MILLISECONDS, Single.error(Throwable()))

    fun addCategory(nameCategory: FormBody): Single<CategoryDto> =
            casherRestApi.addCategory(nameCategory)
                    .exponentialRetry(ADD_DATA_TIMEOUT, Backoff(maxDelay = ADD_DATA_TIMEOUT))
                    .timeout(ADD_DATA_TIMEOUT, TimeUnit.MILLISECONDS, Single.error(Throwable()))

    companion object {
        private var instance: PurchaseRepository? = null

        private val CATEGORY_TIMEOUT = TimeUnit.SECONDS.toMillis(30)
        private val PAYMENT_TIMEOUT = TimeUnit.SECONDS.toMillis(45)
        private val ADD_DATA_TIMEOUT = TimeUnit.SECONDS.toMillis(10)
    }
}