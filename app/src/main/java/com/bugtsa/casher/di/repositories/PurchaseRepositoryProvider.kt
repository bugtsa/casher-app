package com.bugtsa.casher.di.repositories

import com.bugtsa.casher.data.models.PurchaseRemoteRepository
import com.bugtsa.casher.networking.CasherApi
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class PurchaseRepositoryProvider(private val casherApi: CasherApi) : Provider<PurchaseRemoteRepository> {

    override fun get(): PurchaseRemoteRepository {
        return PurchaseRemoteRepository(casherApi)
    }
}