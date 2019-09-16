package com.bugtsa.casher.di.inject.payment

import com.bugtsa.casher.data.local.database.CasherDatabase
import com.bugtsa.casher.data.local.database.entity.payment.PaymentDao
import javax.inject.Provider

class PaymentDaoProvider(private val casherDatabase: CasherDatabase): Provider<PaymentDao> {

    override fun get(): PaymentDao {
        return casherDatabase.paymentDao()
    }
}