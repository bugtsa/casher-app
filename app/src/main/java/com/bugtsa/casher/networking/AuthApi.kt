package com.bugtsa.casher.networking

import com.bugtsa.casher.data.network.CredentialAuthRes
import io.reactivex.Single
import okhttp3.FormBody
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST(OAUTH_TOKEN)
    fun getAuthToken(@Body credential: FormBody): Single<CredentialAuthRes>

    companion object {
        private const val OAUTH_TOKEN = "oauth/token"
    }
}