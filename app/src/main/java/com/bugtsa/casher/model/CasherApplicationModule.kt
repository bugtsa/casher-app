package com.bugtsa.casher.model

import android.app.Application
import android.content.Context
import com.bugtsa.casher.BuildConfig
import com.bugtsa.casher.model.inject.ApplicationProvider
import io.reactivex.disposables.CompositeDisposable
import toothpick.Toothpick.setConfiguration
import toothpick.config.Module
import toothpick.configuration.Configuration.forDevelopment
import toothpick.configuration.Configuration.forProduction

class CasherApplicationModule : Module {

    constructor(application: Application) {
        bind(CompositeDisposable::class.java).toProviderInstance(CompositeDisposableProvider())
        bind(Application::class.java).toProviderInstance(ApplicationProvider(application))
    }
}