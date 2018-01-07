package com.bugtsa.casher.arch.models

import javax.inject.Singleton

@Singleton
class PurchaseModel {

    var sizePurchaseList: Int = 0

    init {
        instance = this
    }

    companion object {

        private var instance: PurchaseModel? = null
    }
}