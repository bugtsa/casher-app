package com.bugtsa.casher.data

import com.bugtsa.casher.data.network.CredentialAuthRes
import com.bugtsa.casher.networking.AuthApi
import io.reactivex.Single
import okhttp3.FormBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val authApi: AuthApi) {
    fun observeCredential(userNameOrEmail: String, password: String): Single<CredentialAuthRes> {
        val credentialFormBody = FormBody.Builder()
                .add(GRANT_TYPE, PASSWORD)
                .add(USER_NAME, userNameOrEmail)
                .add(PASSWORD, password)
        return authApi.observeAuthToken(credentialFormBody.build())
    }

    companion object {
        private const val GRANT_TYPE = "grant_type"
        private const val PASSWORD = "password"
        private const val USER_NAME = "username"
    }
}