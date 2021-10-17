package com.bugtsa.casher.ui.activities

import com.bugtsa.casher.data.prefs.PreferenceRepository
import javax.inject.Inject

class MainPresenter @Inject constructor(
	private val preferenceProvider: PreferenceRepository
) {

	private lateinit var mainView: MainView

	fun onAttachView(mainView: MainView) {
		this.mainView = mainView
	}

	fun saveAccountName(accountName: String) {
		preferenceProvider.saveUserEmail(accountName)
	}
}