package com.bugtsa.casher.di.domain

import com.bugtsa.casher.data.local.database.entity.payment.PaymentDataStore
import com.bugtsa.casher.data.repositories.PurchaseRemoteRepository
import com.bugtsa.casher.domain.interactors.AddPurchaseInteractor
import javax.inject.Provider

class AddPurchaseInteractorProvider(
    private val remotePurchaseRepo: PurchaseRemoteRepository,
    private val localPaymentRepo: PaymentDataStore
): Provider<AddPurchaseInteractor> {
    override fun get(): AddPurchaseInteractor {
        return AddPurchaseInteractor(
            remotePurchaseRepo, localPaymentRepo
        )
    }
}