package com.bugtsa.casher.networking

import com.bugtsa.casher.data.dto.CategoryDto
import com.bugtsa.casher.data.dto.PaymentDto
import com.bugtsa.casher.data.network.CategoryRes
import com.bugtsa.casher.data.network.payment.PaymentRes
import com.bugtsa.casher.data.network.payment.PaymentsByDayRes
import com.bugtsa.casher.data.network.chart.ChartDataRes
import com.bugtsa.casher.data.network.payment.PaymentPageRes
import com.bugtsa.casher.utils.ConstantManager.CategoryNetwork.CATEGORIZED_NAME_METHOD
import com.bugtsa.casher.utils.ConstantManager.CategoryNetwork.CATEGORY_NAME_METHOD
import com.bugtsa.casher.utils.ConstantManager.CategoryNetwork.CHARTS_NAME_METHOD
import com.bugtsa.casher.utils.ConstantManager.Network.LAST_PAGE_PAYMENT_NAME_METHOD
import com.bugtsa.casher.utils.ConstantManager.Network.PAGE_PAYMENT_NAME_METHOD
import com.bugtsa.casher.utils.ConstantManager.Network.PAYMENT_NAME_METHOD
import io.reactivex.Flowable
import io.reactivex.Single
import okhttp3.FormBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface CasherApi {

    @GET(CATEGORY_NAME_METHOD)
    fun getCategories(): Flowable<List<CategoryRes>>

    @POST(CATEGORY_NAME_METHOD)
    fun addCategory(@Body nameCategory: FormBody): Single<CategoryDto>

    @GET("$PAYMENT_NAME_METHOD$PAGE_PAYMENT_NAME_METHOD$LAST_PAGE_PAYMENT_NAME_METHOD")
    fun getPagedPayments(@Header("Authorization") authHeader: String): Flowable<PaymentPageRes>

    @POST("$PAYMENT_NAME_METHOD/full")
    fun addPayment(@Body payment: FormBody): Single<PaymentDto>

    @GET("$CHARTS_NAME_METHOD/rangeMonth")
    fun getRangeMonths(): Single<List<PaymentRes>>

    @GET("$CHARTS_NAME_METHOD/date&user$CATEGORIZED_NAME_METHOD")
    fun getOneMonthChartData(@QueryMap params: Map<String, String>): Single<ChartDataRes>

    @GET("$CHARTS_NAME_METHOD$CATEGORIZED_NAME_METHOD/range")
    fun getRangeMonthChartData(@QueryMap params: Map<String, String>): Single<List<ChartDataRes>>
}