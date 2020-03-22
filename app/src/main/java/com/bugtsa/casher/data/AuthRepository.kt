package com.bugtsa.casher.data

import com.bugtsa.casher.data.network.CredentialAuthRes
import com.bugtsa.casher.di.inject.network.AuthApiProvider.Companion.AUTHORIZATION_CLIENT_APP
import com.bugtsa.casher.networking.AuthApi
import io.reactivex.Single
import okhttp3.FormBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val authApi: AuthApi) {
    fun observeCredential(userNameOrEmail: String, password: String): Single<CredentialAuthRes> {
        val credentialFormBody = FormBody.Builder()
                .add(GRANT_TYPE_HEADER, PASSWORD)
                .add(USER_NAME, userNameOrEmail)
                .add(PASSWORD, password)
        return authApi.observeAuthToken(credentialFormBody.build())
    }

    fun observeRefreshToken(refreshToken: String): Single<CredentialAuthRes> {
        val refreshTokenFormBody = FormBody.Builder()
                .add(GRANT_TYPE_HEADER, REFRESH_GRANT_TYPE)
                .add(REFRESH_GRANT_TYPE, refreshToken)
                .add(CLIENT_ID_HEADER, AUTHORIZATION_CLIENT_APP)
        return authApi.observeAuthToken(refreshTokenFormBody.build())
    }

    companion object {
        private const val GRANT_TYPE_HEADER = "grant_type"
        private const val REFRESH_GRANT_TYPE = "refresh_token"
        private const val CLIENT_ID_HEADER = "client_id"
        private const val PASSWORD = "password"
        private const val USER_NAME = "username"
    }
}