package com.bugtsa.casher.presentation

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bugtsa.casher.domain.prefs.PreferenceRepository
import toothpick.Toothpick
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SingUpViewModelFactory @Inject constructor(private val app: Application) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return Toothpick.openScope(app).getInstance(modelClass) as T
    }
}

class SingUpViewModel @Inject constructor(
        private val preferenceProvider: PreferenceRepository
) : ViewModel() {
    private val actionSingInLiveData = MutableLiveData<Boolean>()
    fun observeActionSingIn(): LiveData<Boolean> = actionSingInLiveData

    private val readyToSignInLiveData = MutableLiveData<Boolean>()
    fun observeReadyToSignIn(): LiveData<Boolean> = readyToSignInLiveData

    private val visibilityCancelLoginBtnLiveData = MutableLiveData<Boolean>()
    fun observeVisibilityLoginCancelBtn(): LiveData<Boolean> = visibilityCancelLoginBtnLiveData

    private val passwordMaybeCancelLiveData = MutableLiveData<Boolean>()
    fun observePasswordMaybeCancel(): LiveData<Boolean> = passwordMaybeCancelLiveData

    fun requestAccountName() {
//        if (!TextUtils.isEmpty(preferenceProvider.getAccountName())) {
//            singUpView.showPurchasesScreen()
//        } else {
//            singUpView.requestAccountName()
//        }
    }

    fun checkReadySignIn(authPair: Pair<String, String>) {
        authPair.also { (login, password) ->
            visibilityCancelLoginBtnLiveData.postValue(login.isNotEmpty())
            passwordMaybeCancelLiveData.postValue(password.isNotEmpty())
            readyToSignInLiveData.value = isReadyToLogin(login, password)
        }
    }

    fun checkLoginPassword(toString: String, toString1: String) {
        TODO("Not yet implemented")
    }

    private fun isReadyToLogin(login: String, password: String): Boolean {
        return login.isNotEmpty() && password.isNotEmpty()
    }
}