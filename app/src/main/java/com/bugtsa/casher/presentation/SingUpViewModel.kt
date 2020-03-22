package com.bugtsa.casher.presentation

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bugtsa.casher.data.AuthRepository
import com.bugtsa.casher.domain.prefs.PreferenceRepository
import com.bugtsa.casher.global.ErrorHandler
import com.bugtsa.casher.global.extentions.AllHidden
import com.bugtsa.casher.global.extentions.KeyboardState
import com.bugtsa.casher.global.extentions.SoftShown
import com.bugtsa.casher.global.rx.SchedulersProvider
import com.bugtsa.casher.presentation.optional.RxAndroidViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import toothpick.Toothpick
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SingUpViewModelFactory @Inject constructor(private val app: Application) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return Toothpick.openScope(app).getInstance(modelClass) as T
    }
}

class SingUpViewModel @Inject constructor(
        private val preferenceRepository: PreferenceRepository,
        private val authRepository: AuthRepository,
        app: Application
) : RxAndroidViewModel(app) {
    private val actionSingInLiveData = MutableLiveData<Boolean>()
    fun observeActionSingIn(): LiveData<Boolean> = actionSingInLiveData

    private val readyToSignInLiveData = MutableLiveData<Boolean>()
    fun observeReadyToSignIn(): LiveData<Boolean> = readyToSignInLiveData

    private val visibilityCancelLoginBtnLiveData = MutableLiveData<Boolean>()
    fun observeVisibilityLoginCancelBtn(): LiveData<Boolean> = visibilityCancelLoginBtnLiveData

    private val passwordMaybeCancelLiveData = MutableLiveData<Boolean>()
    fun observePasswordMaybeCancel(): LiveData<Boolean> = passwordMaybeCancelLiveData

    private val requestEmailLiveData = MutableLiveData<Unit>()
    fun observeRequestEmail(): LiveData<Unit> = requestEmailLiveData

    private val keyboardStateLiveData = MutableLiveData<Pair<FocusFieldAuth, KeyboardState>>().apply { value = FocusFieldAuth.Email to AllHidden }
    fun observeKeyboardState() = keyboardStateLiveData as LiveData<Pair<FocusFieldAuth, KeyboardState>>

    private val keyboardIsShownLiveData = MutableLiveData<Boolean>().apply { value = false }
    fun observeKeyboardIsShown() = keyboardIsShownLiveData as LiveData<Boolean>

    private val validEmailLiveData = MutableLiveData<Boolean>().apply { value = false }
    fun observeCorrectEmail(): LiveData<Boolean> = validEmailLiveData

    init {
//        keyboardInteractor.observeKeyboardIsShown()
//                .distinctUntilChanged()
//                .subscribeOn(SchedulersProvider.io())
//                .observeOn(SchedulersProvider.ui())
//                .subscribe(keyboardIsShownLiveData::setValue, ErrorHandler::handle)
//                .also(::addDispose)
    }

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

    fun checkLoginPassword(login: String, password: String) {
//        validEmailLiveData.value = isValidEmail(login)
        authRepository.observeCredential(login, password)
                .subscribeOn(SchedulersProvider.io())
                .observeOn(SchedulersProvider.ui())
                .subscribe({ res ->
                    res.email?.also { email ->
                        preferenceRepository.saveAccountName(email)
                    }
                }, ErrorHandler::handle)
                .also(::addDispose)
    }

    fun requestEmail() {
        if (isGooglePlayServicesAvailable) {
            requestEmailLiveData.value = Unit
        }
    }

    fun requestShowKeyboard(field: FocusFieldAuth) {
        keyboardStateLiveData.value = field to SoftShown
    }

    private val isGooglePlayServicesAvailable: Boolean
        get() {
            val apiAvailability = GoogleApiAvailability.getInstance()
            val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(getApplication())
            return connectionStatusCode == ConnectionResult.SUCCESS
        }

    private fun isValidEmail(email: String?): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email ?: "")
        return matcher.matches()
    }

    private fun isReadyToLogin(login: String, password: String): Boolean {
        return login.isNotEmpty() && password.isNotEmpty()
    }

    sealed class FocusFieldAuth {
        object Email : FocusFieldAuth()
        object Password : FocusFieldAuth()
    }
}