package com.bugtsa.casher.networking

import com.bugtsa.casher.data.dto.CategoryRes
import com.bugtsa.casher.data.dto.PaymentRes
import io.reactivex.Observable
import retrofit2.http.GET
import java.util.List

interface CasherApi {

    @GET("payment")
    fun getPayments(): Observable<List<PaymentRes>>

    @GET("category")
    fun getCategories(): Observable<List<CategoryRes>>
}