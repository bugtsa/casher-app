package com.bugtsa.casher.di.inject.network

import com.bugtsa.casher.networking.CasherApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Provider

class CasherRestApiProvider: Provider<CasherApi> {

    private val casherApi: CasherApi

    init {
        val okHttpClient = OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(RESOURCE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        casherApi = retrofit.create(CasherApi::class.java)
    }

    override fun get(): CasherApi{
        return casherApi
    }

    companion object {
        private const val READ_TIMEOUT = 20L
        private const val CONNECTION_TIMEOUT = 60L
//        private const val RESOURCE_URL = "https://casher-resource.herokuapp.com/"
        private const val RESOURCE_URL = "http://10.0.2.2:9091/"
    }
}