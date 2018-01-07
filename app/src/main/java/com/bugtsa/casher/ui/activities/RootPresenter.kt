package com.bugtsa.casher.ui.activities

import com.bugtsa.casher.networking.GoogleSheetService
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import javax.inject.Inject

class RootPresenter @Inject constructor(googleSheetService : GoogleSheetService) {

    lateinit var rootView : RootView

    private var credential: GoogleAccountCredential

    init {
        this.credential = googleSheetService.mCredential
    }
    fun onAttachView(rootView: RootView) {
        this.rootView = rootView
    }

    fun requestCredential() {
        rootView.requestToApi(credential)
    }
}