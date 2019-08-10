package com.bugtsa.casher.di.module

import android.app.Application
import com.bugtsa.casher.data.models.PurchaseModel
import com.bugtsa.casher.di.inject.*
import com.bugtsa.casher.data.local.database.entity.category.CategoryDao
import com.bugtsa.casher.data.local.database.entity.category.CategoryDataStore
import com.bugtsa.casher.data.local.prefs.LocalSettingsRepository
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
        bind(CategoryDataStore::class.java).toProviderInstance(
                LocalCategoryDateStoreProvider(
                        categoryDao.get()
                )
        )
    }
}