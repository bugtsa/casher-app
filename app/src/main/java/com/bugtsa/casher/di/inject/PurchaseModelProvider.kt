package com.bugtsa.casher.di.inject

import com.bugtsa.casher.arch.models.PurchaseModel
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class PurchaseModelProvider : Provider<PurchaseModel> {

    private val purchaseModel : PurchaseModel

    constructor() {
        purchaseModel = PurchaseModel()
    }

    override fun get(): PurchaseModel {
        return purchaseModel
    }
}