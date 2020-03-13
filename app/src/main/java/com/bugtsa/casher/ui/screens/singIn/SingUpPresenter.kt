package com.bugtsa.casher.ui.screens.singIn

import android.text.TextUtils
import com.bugtsa.casher.domain.prefs.PreferenceRepository
import javax.inject.Inject

class SingUpPresenter @Inject constructor(
        private val preferenceProvider: PreferenceRepository
) {
    private lateinit var singUpView: SingUpView

    fun onAttachView(singUpView: SingUpView) {
        this.singUpView = singUpView
    }

    fun requestAccountName() {
        if (!TextUtils.isEmpty(preferenceProvider.getAccountName())) {
            singUpView.showPurchasesScreen()
        } else {
            singUpView.requestAccountName()
        }
    }
}