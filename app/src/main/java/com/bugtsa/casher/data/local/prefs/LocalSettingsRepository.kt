package com.bugtsa.casher.data.local.prefs

interface LocalSettingsRepository {

	fun getAccoutnName(): String
	fun saveAccountName(accountName: String)
}