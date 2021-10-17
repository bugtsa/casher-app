package com.bugtsa.casher.di.repositories

import android.app.Application
import com.bugtsa.casher.data.prefs.LocalSettingsRepository
import com.bugtsa.casher.data.prefs.PreferenceRepository
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class PreferenciesRepositoryProvider(
    private val application: Application
) : Provider<LocalSettingsRepository> {

    override fun get(): LocalSettingsRepository = PreferenceRepository(application)

}