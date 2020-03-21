package com.bugtsa.casher.networking

import com.bugtsa.casher.data.network.CredentialAuthRes
import io.reactivex.Single
import okhttp3.FormBody
import retrofit2.http.POST

interface AuthApi {

    @POST("oauth/token")
    fun observeAuthToken(credential: FormBody): Single<CredentialAuthRes>
}