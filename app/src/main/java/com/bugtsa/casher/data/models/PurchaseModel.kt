package com.bugtsa.casher.data.models

import com.bugtsa.casher.data.dto.CategoryDto
import com.bugtsa.casher.data.dto.PurchaseDto
import com.bugtsa.casher.networking.CasherApi
import io.reactivex.Flowable
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

    fun getPaymentsList(): Flowable<List<PurchaseDto>> {
        return casherRestApi.getPayments()
                .flatMapIterable { res -> res }
                .map { res -> PurchaseDto(res.price ?: "", res.date ?: "", res.category ?: "") }
                .toList()
                .toFlowable()
    }
}