package com.bugtsa.casher.domain.prefs

interface LocalSettingsRepository {

	fun getAccountName(): String
	fun saveAccountName(accountName: String)

	fun getUserEmail(): String
	fun saveUserEmail(email: String)

	fun getAccessToken(): String
	fun saveAccessToken(accessToken: String)

	fun getRefreshToken(): String
	fun saveRefreshToken(refreshToken: String)

	fun saveModeTheme(themeMode: String)
	fun getModeTheme(): String
}