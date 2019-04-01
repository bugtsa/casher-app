package com.bugtsa.casher.ui.activities

import android.os.Bundle
import com.bugtsa.casher.networking.CasherApi
import javax.inject.Inject

class RootPresenter @Inject constructor(val casherRestApi:   CasherApi) {

    lateinit var rootView : RootView

    fun onAttachView(rootView: RootView) {
        this.rootView = rootView
    }

    fun requestCredential() {
        rootView.getPayments(casherRestApi.getPayments())
    }
}