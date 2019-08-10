package com.bugtsa.casher.di.inject

import android.app.*
import android.preference.*
import com.bugtsa.casher.data.local.prefs.LocalSettingsRepository
import javax.inject.*

class PreferenceProvider @Inject constructor(application: Application) : Provider<LocalSettingsRepository>, LocalSettingsRepository {

	companion object {
		private const val ACCOUNT_NAME_KEY = "ACCOUNT_NAME_KEY"
	}

	private val settings =
		PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
	private val settingsEditor = settings.edit()

	override fun get(): LocalSettingsRepository {
		return this
	}

	override fun saveAccountName(accountName: String) {
		settingsEditor.putString(ACCOUNT_NAME_KEY, accountName)
			.apply()
	}

	override fun getAccoutnName(): String {
		return settings.getString(ACCOUNT_NAME_KEY, "") ?: ""
	}
}