package com.bugtsa.casher.presentation

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bugtsa.casher.data.prefs.PreferenceRepository
import com.bugtsa.casher.utils.ThemeHelper
import com.bugtsa.casher.utils.ThemeHelper.lightMode
import toothpick.Toothpick
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsViewModelFactory @Inject constructor(private val app: Application) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            Toothpick.openScope(app).getInstance(modelClass) as T
}

class SettingsViewModel @Inject constructor(private val preferenceRepo: PreferenceRepository) : ViewModel() {

    private val modeThemeLiveData = MutableLiveData<Boolean>()
    fun observeModelTheme(): LiveData<Boolean> = modeThemeLiveData

    private val userLoginLiveData = MutableLiveData<String>()
    fun observeUserLogin(): LiveData<String> = userLoginLiveData

    private val logoutLiveData = MutableLiveData<Boolean>()
    fun observeLogout(): LiveData<Boolean> = logoutLiveData

    init {
        processModeTheme(preferenceRepo.getModeTheme())
        requestUserLogin()
    }

    private fun requestUserLogin() {
        userLoginLiveData.value = preferenceRepo.getUserEmail()
    }

    fun saveModeTheme(theme: String) {
        if (preferenceRepo.getModeTheme() != theme) {
            preferenceRepo.saveModeTheme(theme)
            ThemeHelper.applyTheme(theme)
            processModeTheme(theme)
        }
    }

    private fun processModeTheme(currentThemeName: String) {
        modeThemeLiveData.value = currentThemeName != lightMode
    }

    fun logout() {
        preferenceRepo.clearAuthData()
        logoutLiveData.value = true
    }
}