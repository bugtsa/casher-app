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
class PurchaseModel @Inject constructor(private val casherRestApi: CasherApi) {

    companion object {
        private var instance: PurchaseModel? = null
    }

    init {
        instance = this
    }

    fun getCategoriesList(): Flowable<List<CategoryDto>> {
        return casherRestApi.getCategories()
                .map { list ->
                    list.filter { res -> res.id != null && res.name.isNullOrEmpty().not() }
                            .map { res -> CategoryDto(res.id?.toLong() ?: 0L, res.name ?: "") }
                }
    }

    fun getPaymentsByDay(): Flowable<List<PaymentsByDayRes>> =
            casherRestApi.getPagedPayments()

    fun addPayment(payment: FormBody): Single<PaymentDto> =
            casherRestApi.addPayment(payment)

    fun addCategory(nameCategory: FormBody): Single<CategoryDto> =
            casherRestApi.addCategory(nameCategory)

}