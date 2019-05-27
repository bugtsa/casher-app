package com.bugtsa.casher.ui.screens.singIn

import android.text.TextUtils
import com.bugtsa.casher.di.inject.PreferenceProvider
import javax.inject.Inject

class SingUpPresenter @Inject constructor(
        private val preferenceProvider: PreferenceProvider
) {
    lateinit var singUpView: SingUpView

    fun onAttachView(singUpView: SingUpView) {
        this.singUpView = singUpView
    }

    fun requestAccountName() {
        if (!TextUtils.isEmpty(preferenceProvider.getAccoutnName())) {
            singUpView.showMainController()
        } else {
            singUpView.requestAccountName()
        }
    }
}