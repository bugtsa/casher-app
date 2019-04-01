package com.bugtsa.casher.networking

import retrofit2.http.GET

import java.util.List
import com.bugtsa.casher.data.dto.*
import io.reactivex.Observable

interface CasherApi {

    private val PAYMENT_WORD = "payment"
    private val CATEGORY_WORD = "category"

    @GET(PAYMENT_WORD)
    fun getPayments(): Observable<List<PaymentRes>>

    @GET(CATEGORY_WORD)
    fun getCategories(): Observable<List<CategoryRes>>
}