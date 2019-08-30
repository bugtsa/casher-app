package com.bugtsa.casher.networking

import com.bugtsa.casher.data.dto.CategoryRes
import com.bugtsa.casher.data.dto.PaymentRes
import io.reactivex.Flowable
import retrofit2.http.GET
import java.util.List

interface CasherApi {

    @GET("payment/all")
    fun getPayments(): Flowable<List<PaymentRes>>

    @GET("category")
    fun getCategories(): Flowable<List<CategoryRes>>
}