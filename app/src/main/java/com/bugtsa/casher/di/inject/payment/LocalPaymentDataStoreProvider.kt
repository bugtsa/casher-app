package com.bugtsa.casher.di.inject.payment

import com.bugtsa.casher.data.local.database.entity.payment.PaymentDao
import com.bugtsa.casher.data.local.database.entity.payment.PaymentDataStore
import javax.inject.Provider

class LocalPaymentDataStoreProvider(paymentDao: PaymentDao): Provider<PaymentDataStore> {

    private val paymentDataStore: PaymentDataStore = PaymentDataStore(paymentDao)

    override fun get(): PaymentDataStore {
        return paymentDataStore
    }
}