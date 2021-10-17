package com.bugtsa.casher.data.repositories

import com.bugtsa.casher.data.dto.AddPurchaseDto
import com.bugtsa.casher.data.dto.AddPurchaseDto.Companion.toFormBody
import com.bugtsa.casher.data.dto.CategoryDto
import com.bugtsa.casher.domain.models.PaymentModel
import com.bugtsa.casher.data.network.payment.PaymentPageRes
import com.bugtsa.casher.data.network.payment.PaymentPageRes.Companion.NEED_REFRESH_TOKEN
import com.bugtsa.casher.data.network.payment.PaymentRes
import com.bugtsa.casher.data.network.payment.PaymentRes.Companion.toModel
import com.bugtsa.casher.global.extentions.Backoff
import com.bugtsa.casher.global.extentions.exponentialRetry
import com.bugtsa.casher.networking.CasherApi
import io.reactivex.Flowable
import io.reactivex.Single
import okhttp3.FormBody
import retrofit2.HttpException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PurchaseRemoteRepository @Inject constructor(private val casherRestApi: CasherApi) {

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
            .onErrorReturn { t ->
                return@onErrorReturn if (t is HttpException && t.code() == 401) {
                    NEED_REFRESH_TOKEN
                } else {
                    t.message.toString()
                }
                    .let { title ->
                        PaymentPageRes.returnWarning(title)
                    }
            }

    fun addCategory(nameCategory: FormBody): Single<CategoryDto> =
        casherRestApi.addCategory(nameCategory)
            .exponentialRetry(ADD_DATA_TIMEOUT, Backoff(maxDelay = ADD_DATA_TIMEOUT))
            .timeout(ADD_DATA_TIMEOUT, TimeUnit.MILLISECONDS, Single.error(Throwable()))

    fun addPayment(payment: AddPurchaseDto): Single<PaymentModel> =
        casherRestApi.addPayment(payment.toFormBody())
            .map { payment ->
                val (newDate, time) = payment.date?.let {
                    payment.time?.let {
                        payment.date to it
                    } ?: PaymentModel.getDateTimePair(payment.date)

                } ?: PaymentModel.STRING_EMPTY_PAYMENT_FIELD to PaymentModel.STRING_EMPTY_PAYMENT_FIELD
                payment.toModel(newDate, time)
            }
            .exponentialRetry(ADD_DATA_TIMEOUT, Backoff(maxDelay = ADD_DATA_TIMEOUT))
            .timeout(ADD_DATA_TIMEOUT, TimeUnit.MILLISECONDS, Single.error(Throwable()))

    companion object {
        private var instance: PurchaseRemoteRepository? = null

        private val CATEGORY_TIMEOUT = TimeUnit.SECONDS.toMillis(30)
        private val PAYMENT_TIMEOUT = TimeUnit.SECONDS.toMillis(45)
        private val ADD_DATA_TIMEOUT = TimeUnit.SECONDS.toMillis(10)
    }
}