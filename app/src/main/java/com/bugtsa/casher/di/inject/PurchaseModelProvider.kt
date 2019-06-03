package com.bugtsa.casher.di.inject

import com.bugtsa.casher.data.models.PurchaseModel
import com.bugtsa.casher.networking.CasherApi
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class PurchaseModelProvider : Provider<PurchaseModel> {

    private val purchaseModel : PurchaseModel

    constructor(casherApi: CasherApi) {
        purchaseModel = PurchaseModel(casherApi)
    }

    override fun get(): PurchaseModel {
        return purchaseModel
    }
}