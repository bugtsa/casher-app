package com.bugtsa.casher.ui.activities

import com.bugtsa.casher.di.inject.PreferenceProvider
import com.bugtsa.casher.networking.CasherApi
import javax.inject.Inject

class RootPresenter @Inject constructor(
	private val casherRestApi: CasherApi,
	private val preferenceProvider: PreferenceProvider
) {

//	lateinit var rootView: SingUpView
//
//	fun onAttachView(rootView: SingUpView) {
//		this.rootView = rootView
//	}
//
//	fun requestAccountName() {
//		if (!TextUtils.isEmpty(preferenceProvider.getAccoutnName())) {
//			rootView.showMainController()
//		} else {
//			rootView.requestAccountName()
//		}
//	}
//
//	fun saveAccountName(accountName: String) {
//		preferenceProvider.saveAccountName(accountName)
//	}
}