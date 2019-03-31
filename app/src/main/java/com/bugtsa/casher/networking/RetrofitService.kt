package com.bugtsa.casher.networking

import retrofit2.Retrofit
import javax.inject.Singleton
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

@Singleton
class RetrofitService {

    private val retrofit:  Retrofit

    val casherApi: CasherApi

    init {
        retrofit = Retrofit.Builder()
                .baseUrl("https://casher-bugtsa.herokuapp.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        casherApi = retrofit.create(CasherApi::class.java)
    }
}