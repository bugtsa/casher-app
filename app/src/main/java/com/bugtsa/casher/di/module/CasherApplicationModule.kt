package com.bugtsa.casher.di.module

import android.app.Application
import com.bugtsa.casher.data.local.database.CasherDatabase
import com.bugtsa.casher.data.local.database.entity.category.CategoryDao
import com.bugtsa.casher.data.local.database.entity.category.CategoryDataStore
import com.bugtsa.casher.data.local.database.entity.payment.PaymentDao
import com.bugtsa.casher.data.local.database.entity.payment.PaymentDataStore
import com.bugtsa.casher.domain.prefs.LocalSettingsRepository
import com.bugtsa.casher.domain.prefs.PreferenceRepository
import com.bugtsa.casher.data.models.PurchaseModel
import com.bugtsa.casher.di.inject.*
import com.bugtsa.casher.di.inject.category.CategoryDaoProvider
import com.bugtsa.casher.di.inject.category.LocalCategoryDateStoreProvider
import com.bugtsa.casher.di.inject.payment.LocalPaymentDataStoreProvider
import com.bugtsa.casher.di.inject.payment.PaymentDaoProvider
import com.bugtsa.casher.networking.CasherApi
import io.reactivex.disposables.CompositeDisposable
import toothpick.config.Module

class CasherApplicationModule : Module {

    constructor(application: Application) {
        bind(CompositeDisposable::class.java).toProviderInstance(CompositeDisposableProvider())

        val casherApi = CasherRestApiProvider()
        bind(CasherApi::class.java).toProviderInstance(casherApi)

        bind(Application::class.java).toProviderInstance(ApplicationProvider(application))
        bind(LocalSettingsRepository::class.java).toProviderInstance(PreferenceRepository(application))

        bind(PurchaseModel::class.java).toProviderInstance(
                PurchaseModelProvider(
                        casherApi.get()))

        val casherDataBaseProvider = DataBaseProvider(application)
        bind(CasherDatabase::class.java).toProviderInstance(casherDataBaseProvider)

        val categoryDao = CategoryDaoProvider(casherDataBaseProvider.get())
        bind(CategoryDao::class.java).toProviderInstance(categoryDao)
        bind(CategoryDataStore::class.java).toProviderInstance(
                LocalCategoryDateStoreProvider(
                        categoryDao.get()
                )
        )

        val paymentDao = PaymentDaoProvider(casherDataBaseProvider.get())
        bind(PaymentDao::class.java).toProviderInstance(paymentDao)
        bind(PaymentDataStore::class.java).toProviderInstance(
                LocalPaymentDataStoreProvider(
                        paymentDao.get()
                )
        )
    }
}