package com.bugtsa.casher.domain.prefs

import android.app.Application
import android.preference.PreferenceManager
import com.bugtsa.casher.utils.ConstantManager.Constants.EMPTY
import javax.inject.Inject
import javax.inject.Provider

class PreferenceRepository @Inject constructor(application: Application) : Provider<LocalSettingsRepository>, LocalSettingsRepository {

	private val settings =
		PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
	private val settingsEditor = settings.edit()

	override fun get(): LocalSettingsRepository {
		return this
	}

	override fun saveAccountName(accountName: String) {
		saveString(ACCOUNT_NAME_KEY, accountName)
	}

	override fun getAccountName(): String {
		return getString(ACCOUNT_NAME_KEY)
	}

	override fun saveModeTheme(themeMode: String) {
		saveString(THEME_MODE_KEY, themeMode)
	}

	override fun getUserEmail(): String {
		TODO("Not yet implemented")
	}

	override fun saveUserEmail(email: String) {
		TODO("Not yet implemented")
	}

	override fun getAccessToken(): String {
		TODO("Not yet implemented")
	}

	override fun saveAccessToken(accessToken: String) {
		TODO("Not yet implemented")
	}

	override fun getRefreshToken(): String {
		TODO("Not yet implemented")
	}

	override fun saveRefreshToken(refreshToken: String) {
		TODO("Not yet implemented")
	}

	override fun getModeTheme(): String {
		return getString(THEME_MODE_KEY)
	}

	private fun saveString(stringKey: String, string: String){
		settingsEditor.putString(stringKey, string).apply()
	}

	private fun getString(stringKey: String): String {
		return settings.getString(stringKey, EMPTY) ?: EMPTY
	}

	companion object {
		private const val ACCOUNT_NAME_KEY = "ACCOUNT_NAME_KEY"
		private const val USER_EMAIL_KEY = "USER_EMAIL_KEY"
		private const val ACCESS_TOKEN_KEY = "ACCESS_TOKEN_KEY"
		private const val REFRESH_TOKEN_KEY = "REFRESH_TOKEN_KEY"
		const val THEME_MODE_KEY = "THEME_MODE_KEY"
	}
}