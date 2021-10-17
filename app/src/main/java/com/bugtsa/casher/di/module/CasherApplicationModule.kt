package com.bugtsa.casher.di.module

import android.app.Application
import com.bugtsa.casher.data.AuthRepository
import com.bugtsa.casher.data.local.database.CasherDatabase
import com.bugtsa.casher.data.local.database.entity.category.CategoryDao
import com.bugtsa.casher.data.local.database.entity.category.CategoryDataStore
import com.bugtsa.casher.data.local.database.entity.payment.PaymentDao
import com.bugtsa.casher.data.local.database.entity.payment.PaymentDataStore
import com.bugtsa.casher.data.repositories.PurchaseRemoteRepository
import com.bugtsa.casher.data.models.charts.BarChartModel
import com.bugtsa.casher.data.models.charts.ChartModel
import com.bugtsa.casher.data.models.charts.ChooseChartsRepository
import com.bugtsa.casher.di.domain.AddPurchaseInteractorProvider
import com.bugtsa.casher.di.inject.*
import com.bugtsa.casher.di.inject.category.CategoryDaoProvider
import com.bugtsa.casher.di.inject.category.LocalCategoryDateStoreProvider
import com.bugtsa.casher.di.repositories.chart.BarChartRepositoryProvider
import com.bugtsa.casher.di.repositories.chart.ChartRepositoryProvider
import com.bugtsa.casher.di.repositories.chart.ChooseChartsRepositoryProvider
import com.bugtsa.casher.di.inject.network.AuthApiProvider
import com.bugtsa.casher.di.inject.network.CasherRestApiProvider
import com.bugtsa.casher.di.inject.payment.LocalPaymentDataStoreProvider
import com.bugtsa.casher.di.inject.payment.PaymentDaoProvider
import com.bugtsa.casher.di.repositories.AuthRepositoryProvider
import com.bugtsa.casher.di.repositories.PurchaseRepositoryProvider
import com.bugtsa.casher.domain.interactors.AddPurchaseInteractor
import com.bugtsa.casher.domain.prefs.LocalSettingsRepository
import com.bugtsa.casher.domain.prefs.PreferenceRepository
import com.bugtsa.casher.networking.AuthApi
import com.bugtsa.casher.networking.CasherApi
import io.reactivex.disposables.CompositeDisposable
import toothpick.config.Module

class CasherApplicationModule(application: Application) : Module() {

    init {
        bind(CompositeDisposable::class.java).toProviderInstance(CompositeDisposableProvider())
        val casherApi = CasherRestApiProvider()
        bind(CasherApi::class.java).toProviderInstance(casherApi)
        val authApi = AuthApiProvider()
        bind(AuthApi::class.java).toProviderInstance(authApi)
        bind(Application::class.java).toProviderInstance(ApplicationProvider(application))
        bind(LocalSettingsRepository::class.java).toProviderInstance(
            PreferenceRepository(
                application
            )
        )
        bind(AuthRepository::class.java).toProviderInstance(
            AuthRepositoryProvider(authApi.get())
        )
        val paymentRepoProvider = PurchaseRepositoryProvider(casherApi.get())
        bind(PurchaseRemoteRepository::class.java).toProviderInstance(
            paymentRepoProvider
        )
        bind(ChooseChartsRepository::class.java).toProviderInstance(
            ChooseChartsRepositoryProvider(casherApi.get())
        )
        bind(ChartModel::class.java).toProviderInstance(
            ChartRepositoryProvider(casherApi.get())
        )
        bind(BarChartModel::class.java).toProviderInstance(
            BarChartRepositoryProvider(casherApi.get())
        )

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

        val paymentDateStoreProvider = LocalPaymentDataStoreProvider(
            paymentDao.get()
        )
        bind(PaymentDataStore::class.java).toProviderInstance(
            paymentDateStoreProvider
        )

        bind(AddPurchaseInteractor::class.java).toProviderInstance(
            AddPurchaseInteractorProvider(
                paymentRepoProvider.get(),
                paymentDateStoreProvider.get()
            )
        )
    }
}