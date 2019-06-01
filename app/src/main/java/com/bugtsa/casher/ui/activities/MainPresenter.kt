package com.bugtsa.casher.ui.activities

import com.bugtsa.casher.di.inject.PreferenceProvider
import javax.inject.Inject

class MainPresenter @Inject constructor(
	private val preferenceProvider: PreferenceProvider
) {

	lateinit var mainView: MainView

	fun onAttachView(mainView: MainView) {
		this.mainView = mainView
	}

	fun saveAccountName(accountName: String) {
		preferenceProvider.saveAccountName(accountName)
	}
}