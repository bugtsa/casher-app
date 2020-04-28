package com.bugtsa.casher.data

import com.bugtsa.casher.data.dto.AuthDto
import com.bugtsa.casher.data.network.CredentialAuthRes
import com.bugtsa.casher.di.inject.network.AuthApiProvider.Companion.AUTHORIZATION_CLIENT_APP
import com.bugtsa.casher.global.extentions.Backoff
import com.bugtsa.casher.global.extentions.exponentialRetry
import com.bugtsa.casher.networking.AuthApi
import io.reactivex.Single
import okhttp3.FormBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepository @Inject constructor(private val authApi: AuthApi) {

    fun observeCredential(userNameOrEmail: String, password: String): Single<AuthDto> {
        val credentialFormBody = FormBody.Builder()
                .add(GRANT_TYPE_HEADER, PASSWORD)
                .add(USER_NAME, userNameOrEmail)
                .add(PASSWORD, password)
        return authApi.observeAuthToken(credentialFormBody.build())
//                .exponentialRetry(AUTH_TIMEOUT, Backoff(maxDelay = AUTH_TIMEOUT))
//                .timeout(AUTH_TIMEOUT, TimeUnit.MILLISECONDS, Single.error(Throwable()))
                .map { res -> AuthDto(res) }
    }

    fun observeRefreshToken(refreshToken: String): Single<AuthDto> {
        val refreshTokenFormBody = FormBody.Builder()
                .add(GRANT_TYPE_HEADER, REFRESH_GRANT_TYPE)
                .add(REFRESH_GRANT_TYPE, refreshToken)
                .add(CLIENT_ID_HEADER, AUTHORIZATION_CLIENT_APP)
        return authApi.observeAuthToken(refreshTokenFormBody.build())
                .exponentialRetry(AUTH_TIMEOUT, Backoff(maxDelay = AUTH_TIMEOUT))
                .timeout(AUTH_TIMEOUT, TimeUnit.MILLISECONDS, Single.error(Throwable()))
                .map { res -> AuthDto(res) }

    }

    

    companion object {
        private const val GRANT_TYPE_HEADER = "grant_type"
        private const val REFRESH_GRANT_TYPE = "refresh_token"
        private const val CLIENT_ID_HEADER = "client_id"
        private const val PASSWORD = "password"
        private const val USER_NAME = "username"
        private val AUTH_TIMEOUT = TimeUnit.SECONDS.toMillis(30)
    }
}