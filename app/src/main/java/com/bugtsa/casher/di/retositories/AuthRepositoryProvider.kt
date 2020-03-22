package com.bugtsa.casher.di.retositories

import com.bugtsa.casher.data.AuthRepository
import com.bugtsa.casher.networking.AuthApi
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class AuthRepositoryProvider(private val authApi: AuthApi) : Provider<AuthRepository> {

    override fun get(): AuthRepository {
        return AuthRepository(authApi)
    }
}