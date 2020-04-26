package com.bugtsa.casher

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.bugtsa.casher.di.module.CasherApplicationModule
import com.bugtsa.casher.domain.prefs.PreferenceRepository
import com.bugtsa.casher.domain.prefs.PreferenceRepository.Companion.THEME_MODE_KEY
import com.bugtsa.casher.utils.ThemeHelper
import com.bugtsa.casher.utils.ThemeHelper.applyTheme
import toothpick.Toothpick
import toothpick.Toothpick.setConfiguration
import toothpick.configuration.Configuration.forDevelopment
import toothpick.configuration.Configuration.forProduction

class CasherApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = this.getSharedPreferences(PreferenceRepository.SETTINGS_PREF_NAME, Context.MODE_PRIVATE)
        val themePref = sharedPreferences.getString(THEME_MODE_KEY, ThemeHelper.default) ?: ThemeHelper.default
        applyTheme(themePref)

        if (BuildConfig.DEBUG) {
            setConfiguration(forDevelopment().preventMultipleRootScopes())
        } else {
            setConfiguration(forProduction())
        }

        val appScope = Toothpick.openScope(this)
        appScope.installModules(CasherApplicationModule(this))
    }
}