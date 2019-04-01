package com.bugtsa.casher.di.module

import android.app.Application
import com.bugtsa.casher.arch.models.PurchaseModel
import com.bugtsa.casher.data.CasherDatabase
import com.bugtsa.casher.data.CategoryDao
import com.bugtsa.casher.data.LocalCategoryDataStore
import com.bugtsa.casher.di.inject.*
import com.bugtsa.casher.networking.CasherApi
import io.reactivex.disposables.CompositeDisposable
import toothpick.config.Module

class CasherApplicationModule : Module {

    constructor(application: Application) {
        bind(CompositeDisposable::class.java).toProviderInstance(CompositeDisposableProvider())
        bind(CasherApi::class.java).toProviderInstance(CasherRestApiProvider())

        bind(Application::class.java).toProviderInstance(ApplicationProvider(application))
        bind(PurchaseModel::class.java).toProviderInstance(PurchaseModelProvider())

        val categoryDao = CategoryDaoProvider(application)
        bind(CategoryDao::class.java).toProviderInstance(categoryDao)
        bind(LocalCategoryDataStore::class.java).toProviderInstance(LocalCategoryDateStoreProvider(categoryDao.get()))
    }
}