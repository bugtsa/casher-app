package com.bugtsa.casher.data.prefs

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.bugtsa.casher.data.dto.AuthDto
import com.bugtsa.casher.utils.ConstantManager.Constants.EMPTY
import com.bugtsa.casher.utils.ThemeHelper.default
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class PreferenceRepository @Inject constructor(application: Application) : LocalSettingsRepository {

    private val settings: SharedPreferences =
        application.getSharedPreferences(SETTINGS_PREF_NAME, Context.MODE_PRIVATE)
    private val settingsEditor = settings.edit()

    override fun saveModeTheme(themeMode: String) {
        saveString(THEME_MODE_KEY, themeMode)
    }

    override fun getUserEmail(): String = getString(USER_EMAIL_KEY)

    override fun getAccessToken(): String = getString(ACCESS_TOKEN_KEY)

    override fun getRefreshToken(): String = getString(REFRESH_TOKEN_KEY)

    override fun getModeTheme(): String = getString(THEME_MODE_KEY)

    override fun getCustomDate(): String = getString(CUSTOM_DATE_PAYMENT)

    override fun saveAuthData(authDto: AuthDto) {
        saveUserEmail(authDto.email)
        saveAccessToken(authDto.accessToken)
        saveRefreshToken(authDto.refreshToken)
    }

    override fun clearAuthData() {
        saveUserEmail(EMPTY)
        saveAccessToken(EMPTY)
        saveRefreshToken(EMPTY)
        saveCustomDate(EMPTY)
        saveModeTheme(default)
    }

    override fun saveUserEmail(email: String) {
        saveString(USER_EMAIL_KEY, email)
    }

    override fun saveCustomDate(customDate: String) {
        saveString(CUSTOM_DATE_PAYMENT, customDate)
    }

    private fun saveAccessToken(accessToken: String) {
        saveString(ACCESS_TOKEN_KEY, accessToken)
    }

    private fun saveRefreshToken(refreshToken: String) {
        saveString(REFRESH_TOKEN_KEY, refreshToken)
    }

    private fun saveString(stringKey: String, string: String) {
        settingsEditor.putString(stringKey, string).apply()
    }

    private fun getString(stringKey: String): String =
        settings.getString(stringKey, EMPTY) ?: EMPTY

    companion object {
        private const val USER_EMAIL_KEY = "USER_EMAIL_KEY"
        private const val ACCESS_TOKEN_KEY = "ACCESS_TOKEN_KEY"
        private const val REFRESH_TOKEN_KEY = "REFRESH_TOKEN_KEY"
        private const val CUSTOM_DATE_PAYMENT = "CUSTOM_DATE_PAYMENT"
        const val THEME_MODE_KEY = "THEME_MODE_KEY"

        const val SETTINGS_PREF_NAME = "settingsPreference"
    }
}