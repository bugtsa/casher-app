package com.bugtsa.casher.networking

import retrofit2.http.GET

import java.util.List
import com.bugtsa.casher.data.dto.*
import io.reactivex.Observable

interface CasherApi {

    @GET("payment")
    fun getPayments(): Observable<List<PaymentRes>>

    @GET("category")
    fun getCategories(): Observable<List<CategoryRes>>
}