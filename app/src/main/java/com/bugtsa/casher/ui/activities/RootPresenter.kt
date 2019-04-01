package com.bugtsa.casher.ui.activities

import android.text.*
import com.bugtsa.casher.di.inject.*
import com.bugtsa.casher.networking.*
import javax.inject.*

class RootPresenter @Inject constructor(
	private val casherRestApi: CasherApi,
	private val preferenceProvider: PreferenceProvider
) {

	lateinit var rootView: RootView

	fun onAttachView(rootView: RootView) {
		this.rootView = rootView
	}

	fun requestAccountName() {
		if (!TextUtils.isEmpty(preferenceProvider.getAccoutnName())) {
			rootView.showMainController()
		} else {
			rootView.requestAccountName()
		}
	}

	fun saveAccountName(accountName: String) {
		preferenceProvider.saveAccountName(accountName)
	}
}