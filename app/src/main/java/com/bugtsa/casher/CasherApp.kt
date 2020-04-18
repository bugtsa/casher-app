package com.bugtsa.casher

import android.content.Context
import android.preference.PreferenceManager
import androidx.multidex.MultiDexApplication
import com.bugtsa.casher.di.module.CasherApplicationModule
import com.bugtsa.casher.domain.prefs.PreferenceRepository
import com.bugtsa.casher.domain.prefs.PreferenceRepository.Companion.THEME_MODE_KEY
import com.bugtsa.casher.utils.ThemeHelper
import com.bugtsa.casher.utils.ThemeHelper.applyTheme
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

        val sharedPreferences = this.getSharedPreferences(PreferenceRepository.SETTINGS_PREF_NAME, Context.MODE_PRIVATE)
        val themePref = sharedPreferences.getString(THEME_MODE_KEY, ThemeHelper.default) ?: ThemeHelper.default
        applyTheme(themePref)

        val configuration = if (BuildConfig.DEBUG) forDevelopment() else forProduction()
        setConfiguration(configuration.disableReflection())
        FactoryRegistryLocator.setRootRegistry(FactoryRegistry())
        MemberInjectorRegistryLocator.setRootRegistry(MemberInjectorRegistry())

        Stetho.initializeWithDefaults(this);

        val appScope = Toothpick.openScope(this)
        appScope.installModules(CasherApplicationModule(this))
    }
}