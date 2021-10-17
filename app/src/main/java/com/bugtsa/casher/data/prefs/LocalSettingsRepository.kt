package com.bugtsa.casher.data.prefs

import com.bugtsa.casher.data.dto.AuthDto

interface LocalSettingsRepository {

	fun saveAuthData(authDto: AuthDto)
	fun clearAuthData()

	fun getUserEmail(): String
	fun saveUserEmail(email: String)

	fun getAccessToken(): String

	fun getRefreshToken(): String

	fun saveModeTheme(themeMode: String)
	fun getModeTheme(): String

	fun saveCustomDate(customDate: String)
	fun getCustomDate(): String
}