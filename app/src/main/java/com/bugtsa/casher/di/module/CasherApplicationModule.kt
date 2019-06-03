package com.bugtsa.casher.di.module

import android.app.Application
import com.bugtsa.casher.data.models.PurchaseModel
import com.bugtsa.casher.di.inject.*
import com.bugtsa.casher.domain.local.database.CategoryDao
import com.bugtsa.casher.domain.local.database.LocalCategoryDataStore
import com.bugtsa.casher.domain.local.preference.LocalSettingsRepository
import com.bugtsa.casher.networking.CasherApi
import io.reactivex.disposables.CompositeDisposable
import toothpick.config.Module

class CasherApplicationModule : Module {

    constructor(application: Application) {
        bind(CompositeDisposable::class.java).toProviderInstance(CompositeDisposableProvider())

        val casherApi = CasherRestApiProvider()
        bind(CasherApi::class.java).toProviderInstance(casherApi)

        bind(Application::class.java).toProviderInstance(ApplicationProvider(application))
        bind(LocalSettingsRepository::class.java).toProviderInstance(PreferenceProvider(application))

        bind(PurchaseModel::class.java).toProviderInstance(
                PurchaseModelProvider(
                        casherApi.get()))

        val categoryDao = CategoryDaoProvider(application)
        bind(CategoryDao::class.java).toProviderInstance(categoryDao)
        bind(LocalCategoryDataStore::class.java).toProviderInstance(
                LocalCategoryDateStoreProvider(
                        categoryDao.get()
                )
        )
    }
}