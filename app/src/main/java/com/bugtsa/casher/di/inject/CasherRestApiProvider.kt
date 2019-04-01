package com.bugtsa.casher.di.inject

import com.bugtsa.casher.networking.CasherApi
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Provider

class CasherRestApiProvider: Provider<CasherApi> {

    val casherApi: CasherApi

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://casher-bugtsa.herokuapp.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        casherApi = retrofit.create(CasherApi::class.java)
    }

    override fun get(): CasherApi{
        return casherApi
    }
}