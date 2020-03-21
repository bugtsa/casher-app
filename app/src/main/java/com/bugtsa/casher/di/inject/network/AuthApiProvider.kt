package com.bugtsa.casher.di.inject.network

import com.bugtsa.casher.networking.AuthApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Provider

class AuthApiProvider: Provider<AuthApi> {

    private val authApi: AuthApi

    init {
        val okHttpClient = OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(AUTH_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        authApi = retrofit.create(AuthApi::class.java)
    }

    override fun get(): AuthApi {
        return authApi
    }

    companion object {
        private const val READ_TIMEOUT = 15L
        private const val CONNECTION_TIMEOUT = 40L
        private const val AUTH_URL = "https://casher-auth.herokuapp.com/"
    }
}