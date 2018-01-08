package com.bugtsa.casher.di.module

import android.app.Application
import com.bugtsa.casher.arch.models.PurchaseModel
import com.bugtsa.casher.di.inject.ApplicationProvider
import com.bugtsa.casher.di.inject.CompositeDisposableProvider
import com.bugtsa.casher.di.inject.PurchaseModelProvider
import io.reactivex.disposables.CompositeDisposable
import toothpick.config.Module

class CasherApplicationModule : Module {

    constructor(application: Application) {
        bind(CompositeDisposable::class.java).toProviderInstance(CompositeDisposableProvider())
        bind(Application::class.java).toProviderInstance(ApplicationProvider(application))
        bind(PurchaseModel::class.java).toProviderInstance(PurchaseModelProvider())
    }
}