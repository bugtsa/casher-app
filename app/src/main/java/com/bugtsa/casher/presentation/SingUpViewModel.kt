package com.bugtsa.casher.presentation

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bugtsa.casher.data.AuthRepository
import com.bugtsa.casher.data.error.AuthApiError
import com.bugtsa.casher.domain.prefs.PreferenceRepository
import com.bugtsa.casher.global.ErrorHandler
import com.bugtsa.casher.global.extentions.AllHidden
import com.bugtsa.casher.global.extentions.KeyboardState
import com.bugtsa.casher.global.extentions.SoftShown
import com.bugtsa.casher.global.rx.SchedulersProvider
import com.bugtsa.casher.presentation.optional.ProgressState
import com.bugtsa.casher.presentation.optional.RxAndroidViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.gson.Gson
import retrofit2.HttpException
import toothpick.Toothpick
import java.net.UnknownHostException
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

    private val routeToPaymentsViewLiveData = MutableLiveData<Boolean>()
    fun observeRouteToPaymentsView(): LiveData<Boolean> = routeToPaymentsViewLiveData

    private val removeHintErrorLiveData = MutableLiveData<Boolean>()
    fun observeRemoveHintErrorLiveData(): LiveData<Boolean> = removeHintErrorLiveData

    private val authErrorLiveData = MutableLiveData<AuthErrorState>()
    fun observeAuthError(): LiveData<AuthErrorState> = authErrorLiveData

    init {
//        keyboardInteractor.observeKeyboardIsShown()
//                .distinctUntilChanged()
//                .subscribeOn(SchedulersProvider.io())
//                .observeOn(SchedulersProvider.ui())
//                .subscribe(keyboardIsShownLiveData::setValue, ErrorHandler::handle)
//                .also(::addDispose)
    }

    fun checkReadySignIn(authPair: Pair<String, String>) {
        authPair.also { (login, password) ->
            removeHintErrorLiveData.value = true
            visibilityCancelLoginBtnLiveData.value = login.isNotEmpty()
            passwordMaybeCancelLiveData.value = password.isNotEmpty()
            readyToSignInLiveData.value = isReadyToLogin(login, password)
        }
    }

    fun checkLoginPassword(login: String, password: String) {
        progressStateLiveData.value = ProgressState.Progress(isCancelable = true)
        authRepository.observeCredential(login, password)
                .subscribeOn(SchedulersProvider.io())
                .observeOn(SchedulersProvider.ui())
                .subscribe({ authDto ->
                    progressStateLiveData.value = ProgressState.Hide
                    preferenceRepository.saveAuthData(authDto)
                    routeToPaymentsViewLiveData.value = true
                }, { throwable ->
                    progressStateLiveData.value = ProgressState.Hide
                    when (throwable) {
                        is UnknownHostException -> {
                            authErrorLiveData.value = AuthErrorState.ConnectionToServer
                        }
                        else -> processAuthError(throwable)
                    }
                })
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

    private fun processAuthError(throwable: Throwable) {
        if (throwable is HttpException && throwable.code() == UNAUTORIZED_USER) {
            val response = throwable.response()
            val message: AuthApiError = Gson().fromJson(response?.errorBody()?.charStream(), AuthApiError::class.java)
            if (message.errorDescription?.contains(IS_NOT_KNOWN_ERROR_DESCRIPTION) == true) {
                val userNameOrEmail = message.errorDescription.replace(USER_ERROR_DESCRIPTION, EMPTY_REPLACE)
                        .replace(IS_NOT_KNOWN_ERROR_DESCRIPTION, EMPTY_REPLACE)
                        .trim()
                authErrorLiveData.value = AuthErrorState.WrongUserCredential(userNameOrEmail)
            } else {
                authErrorLiveData.value = AuthErrorState.Unknown
            }
        } else {
            authErrorLiveData.value = AuthErrorState.Unknown
        }
        ErrorHandler.handle(throwable)
    }

    sealed class FocusFieldAuth {
        object Email : FocusFieldAuth()
        object Password : FocusFieldAuth()
    }

    sealed class AuthErrorState {
        object ConnectionToServer : AuthErrorState()
        object Unknown : AuthErrorState()
        data class WrongUserCredential(val email: String) : AuthErrorState()
    }

    companion object {
        private const val USER_ERROR_DESCRIPTION = "User"
        private const val IS_NOT_KNOWN_ERROR_DESCRIPTION = "is not known"
        private const val EMPTY_REPLACE = ""
        private const val UNAUTORIZED_USER = 401
    }
}