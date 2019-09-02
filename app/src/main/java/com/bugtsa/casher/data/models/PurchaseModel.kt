package com.bugtsa.casher.data.models

import com.bugtsa.casher.data.dto.CategoryDto
import com.bugtsa.casher.data.dto.PaymentDto
import com.bugtsa.casher.data.dto.PaymentsByDayRes
import com.bugtsa.casher.networking.CasherApi
import io.reactivex.Flowable
import io.reactivex.Single
import okhttp3.FormBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PurchaseModel @Inject constructor(injectCasherRestApi: CasherApi) {

    companion object {
        private var instance: PurchaseModel? = null
    }

    init {
        instance = this
    }

    var sizePurchaseList: Int = 0
    private val casherRestApi = injectCasherRestApi

    fun getCategoriesList(): Flowable<List<CategoryDto>> {
        return casherRestApi.getCategories()
                .flatMapIterable { res -> res }
                .map { res -> CategoryDto(res.id?.toLong() ?: 0L, res.name ?: "") }
                .toList()
                .toFlowable()
    }

    fun getPaymentsByDay(): Flowable<List<PaymentsByDayRes>> =
            casherRestApi.getPagedPayments()

    fun addPayment(payment: FormBody): Single<PaymentDto> =
            casherRestApi.addPayment(payment)

    fun addCategory(nameCategory: FormBody): Single<CategoryDto> =
            casherRestApi.addCategory(nameCategory)

}