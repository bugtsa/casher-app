package com.bugtsa.casher.di.repositories

import com.bugtsa.casher.data.models.PurchaseRepository
import com.bugtsa.casher.networking.CasherApi
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class PurchaseRepositoryProvider(private val casherApi: CasherApi) : Provider<PurchaseRepository> {

    override fun get(): PurchaseRepository {
        return PurchaseRepository(casherApi)
    }
}