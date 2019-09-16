package com.bugtsa.casher.di.inject

import com.bugtsa.casher.data.models.PurchaseModel
import com.bugtsa.casher.networking.CasherApi
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class PurchaseModelProvider(private val casherApi: CasherApi) : Provider<PurchaseModel> {

    override fun get(): PurchaseModel {
        return PurchaseModel(casherApi)
    }
}