package com.bugtsa.casher.domain.local.preference

interface LocalSettingsRepository {

	fun getAccoutnName(): String
	fun saveAccountName(accountName: String)
}