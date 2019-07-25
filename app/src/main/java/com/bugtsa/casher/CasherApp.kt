package com.bugtsa.casher

import androidx.multidex.MultiDexApplication
import com.bugtsa.casher.di.module.CasherApplicationModule
import com.facebook.stetho.Stetho
import toothpick.Toothpick
import toothpick.Toothpick.setConfiguration
import toothpick.configuration.Configuration.forDevelopment
import toothpick.configuration.Configuration.forProduction
import toothpick.registries.FactoryRegistryLocator
import toothpick.registries.MemberInjectorRegistryLocator

class CasherApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        val configuration = if (BuildConfig.DEBUG) forDevelopment() else forProduction()
        setConfiguration(configuration.disableReflection())
        FactoryRegistryLocator.setRootRegistry(FactoryRegistry())
        MemberInjectorRegistryLocator.setRootRegistry(MemberInjectorRegistry())

        Stetho.initializeWithDefaults(this);

        val appScope = Toothpick.openScope(this)
        appScope.installModules(CasherApplicationModule(this))
    }
}