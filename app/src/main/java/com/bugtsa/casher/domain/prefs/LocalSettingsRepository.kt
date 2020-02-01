package com.bugtsa.casher.domain.prefs

interface LocalSettingsRepository {

	fun getAccountName(): String
	fun saveAccountName(accountName: String)

	fun saveModeTheme(themeMode: String)
	fun getModeTheme(): String
}