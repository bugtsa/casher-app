package com.bugtsa.casher.ui.screens.singIn

import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bugtsa.casher.R
import com.bugtsa.casher.global.extentions.*
import com.bugtsa.casher.presentation.SingUpViewModel
import com.bugtsa.casher.presentation.SingUpViewModel.FocusFieldAuth.Email
import com.bugtsa.casher.presentation.SingUpViewModel.FocusFieldAuth.Password
import com.bugtsa.casher.presentation.SingUpViewModelFactory
import com.bugtsa.casher.presentation.optional.ProgressState.Hide
import com.bugtsa.casher.presentation.optional.ProgressState.Progress
import com.bugtsa.casher.ui.activities.MainActivity
import com.bugtsa.casher.ui.screens.BaseFragment
import com.bugtsa.casher.ui.screens.purchases.show.PurchasesScreen
import com.bugtsa.casher.ui.screens.settings.NavigationStackPresentable
import com.bugtsa.casher.utils.ConstantManager.Constants.EMPTY
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.common.AccountPicker
import com.google.android.gms.common.GoogleApiAvailability
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_auth.*
import pro.horovodovodo4ka.bones.Phalanx
import pro.horovodovodo4ka.bones.extensions.show
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.ui.FragmentSibling
import pro.horovodovodo4ka.bones.ui.delegates.Page
import toothpick.Toothpick
import java.util.concurrent.TimeUnit

class SingUpScreen : Phalanx(), NavigationStackPresentable {
    override val seed = { SingUpFragment() }

    override val fragmentTitle: String
        get() = ""
}

@SuppressLint("MissingSuperCall")
class SingUpFragment(override val layout: Int = R.layout.fragment_auth) : BaseFragment(),
        BonePersisterInterface<SingUpScreen>,
        FragmentSibling<SingUpScreen> by Page(),
        SingUpView {

    private lateinit var viewModel: SingUpViewModel

    private val bag = CompositeDisposable()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val singUpViewModelFactory = Toothpick.openScopes(activity, this)
                .getInstance(SingUpViewModelFactory::class.java)
        viewModel = ViewModelProvider(this, singUpViewModelFactory)[SingUpViewModel::class.java]

        initClickListeners()
        bindViews()
        bindViewModel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vLogin.setAutofillHints(View.AUTOFILL_HINT_USERNAME)
            vPassword.setAutofillHints(View.AUTOFILL_HINT_PASSWORD)
        }

        refreshUI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super<BonePersisterInterface>.onSaveInstanceState(outState)
        super<BaseFragment>.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<BonePersisterInterface>.onCreate(savedInstanceState)
        super<BaseFragment>.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        hideKeyboard()
        bag.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EMAIL && resultCode == Activity.RESULT_OK) {
            val accountName = data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            vLogin.setText(accountName)
        }
    }

    //region ================= SingUpView methods =================

    override fun requestAccountName() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(activity)
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode)
        } else {
            accountNameIntent()
        }
    }

    //endregion

    //region ================= Request Play Services =================

    override fun showPurchasesScreen() {
        bone.show(PurchasesScreen())
    }

    private fun accountNameIntent() {
        try {
            val intent = AccountPicker.newChooseAccountIntent(
                    null, null,
                    arrayOf(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE), false, null, null, null, null
            )
            startActivityForResult(intent, MainActivity.REQUEST_CODE_EMAIL)
        } catch (e: ActivityNotFoundException) {
            Log.e("SingUpActivity", e.toString())
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     * Google Play Services on this device.
     */
    private fun showGooglePlayServicesAvailabilityErrorDialog(
            connectionStatusCode: Int
    ) {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val dialog = apiAvailability.getErrorDialog(
                activity,
                connectionStatusCode,
                MainActivity.REQUEST_GOOGLE_PLAY_SERVICES
        )
        dialog.show()
    }

    //endregion

    private fun bindViewModel() {
        viewModel.observeReadyToSignIn().observe(viewLifecycleOwner, Observer(::processReadyLoginButton))

        viewModel.observeRequestEmail().observe(viewLifecycleOwner, Observer {
            requestUserEmail()
        })

        viewModel.observeKeyboardState().observe(viewLifecycleOwner, Observer { (field, state) ->
            val view = when (field) {
                Email -> vLogin
                Password -> vPassword
            }
            when (state) {
                AllHidden -> {
                    view.requestFocus()
                    requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                    hideKeyboard()
                }
                SoftShown -> {
                    if (viewModel.observeKeyboardIsShown().value == false) {
                        view.requestFocus()
                        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                        showKeyboard(view)
                    }
                }
            }
        })

        viewModel.observeCorrectEmail().observe(viewLifecycleOwner, Observer { isCorrectEmail ->
            if (isCorrectEmail) {
                setupAuthError(loginErrorText = getString(R.string.auth_need_input_valid_email))
            } else {
                setupCorrectHints()
            }
        })

        viewModel.observeRouteToPaymentsView().observe(viewLifecycleOwner, Observer {
            showPurchasesScreen()
        })

        viewModel.observeWrongUserCredential().observe(viewLifecycleOwner, Observer { wrongInput ->
            setupAuthError(loginErrorText = getString(R.string.auth_wrong_login_or_password, wrongInput))
        })

        viewModel.observerProgressStateLiveData().observe(viewLifecycleOwner, Observer { progressState ->
            when (progressState) {
                is Progress ->
                    showProgress(cancelable = progressState.isCancelable)
                is Hide -> hideProgress()
            }
        })

        viewModel.observeRemoveHintErrorLiveData().observe(viewLifecycleOwner, Observer { isRemove ->
            if(isRemove){
                setupCorrectHints()
            }
        })
    }

    private fun bindViews() {
        vLogin.setupLoginChangesListener(R.color.colorAccent)
        vPassword.setupPasswordChangesListener(R.color.colorAccent)
    }

    private fun processReadyLoginButton(isReady: Boolean) {
        val stateStartLoginButton = when (isReady) {
            true -> ReadyToLogin.ReadyAuthEnter(true,
                    R.drawable.button_background_state_enabled,
                    R.color.colorPrimary)
            false -> ReadyToLogin.NotReadyAuthEnter(false,
                    R.drawable.button_background_state_disabled,
                    R.color.primaryTextColor)
        }

        vSingIn.isEnabled = stateStartLoginButton.isEnable
        vSingIn.background = ContextCompat.getDrawable(requireContext(), stateStartLoginButton.backgroundInt)
        vSingIn.setTextColor(ContextCompat.getColor(requireContext(), stateStartLoginButton.textColorInt))
    }

    private fun EditText.setupLoginChangesListener(colorAccentResId: Int) {
        RxTextView.textChanges(this)
                .debounce(TIME_DURATION_DEBOUNCE, TimeUnit.MILLISECONDS)
                .map { login ->
                    login.toString().trim() to vPassword.text.toString().trim()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(viewModel::checkReadySignIn)
                .addTo(bag)

        viewModel.observeVisibilityLoginCancelBtn().observe(viewLifecycleOwner, Observer { isVisible ->
            val drawable = if (isVisible) {
                getDrawable(
                        colorAccentResId,
                        R.drawable.ic_cancel,
                        requireContext())
            } else null

            setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        })

        setOnTouchListener(getOnTouchIncludeRightDrawableClick(
                {
                    if (text.toString().isEmpty()) {
                        if (hasFocus()) {
                            viewModel.requestEmail()
                        } else {
                            viewModel.requestShowKeyboard(Email)
                            requestFocus()
                        }
                    }
                },
                { resetInput() }
        ))
    }

    private fun setupAuthError(loginErrorText: String = ERROR_EMPTY_EMAIL, passwordError: String = ERROR_EMPTY_EMAIL) {
        vLoginTextInputLayout.error = loginErrorText
        vPasswordTextInputLayout.error = passwordError
    }

    private fun setupCorrectHints() {
        vLoginTextInputLayout.error = EMPTY
        vPasswordTextInputLayout.error = EMPTY
    }

    private fun requestUserEmail() {
        try {
            val intent = AccountPicker.newChooseAccountIntent(null, null,
                    arrayOf(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE), false, null, null, null, null)
            startActivityForResult(intent, REQUEST_CODE_EMAIL)
        } catch (e: ActivityNotFoundException) {

        }
    }

    private fun EditText.setupPasswordChangesListener(colorAccentResId: Int) {
        var isPasswordVisible = false
        setupPassVisibility()
        RxTextView.textChanges(this)
                .debounce(TIME_DURATION_DEBOUNCE, TimeUnit.MILLISECONDS)
                .map { password ->
                    vLogin.text.toString().trim() to password.toString().trim()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(viewModel::checkReadySignIn)
                .addTo(bag)

        viewModel.observePasswordMaybeCancel().observe(viewLifecycleOwner, Observer { isMaybeCancel ->
            if (isMaybeCancel) {
                showStatePassVisibility(colorAccentResId,
                        isPasswordVisible,
                        requireContext())
            } else {
                setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
        })
        setOnTouchListener(getOnTouchRightDrawableClickListener {
            isPasswordVisible = togglePassVisibility(isPasswordVisible)
            showStatePassVisibility(colorAccentResId,
                    isPasswordVisible,
                    requireContext())
        })
    }

    private fun initClickListeners() {
        vSingIn.setOnClickListener {
            viewModel.checkLoginPassword(vLogin.text.toString(), vPassword.text.toString())
            hideKeyboard()
        }
        vPassword.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_GO) {
                viewModel.checkLoginPassword(vLogin.text.toString(), vPassword.text.toString())
                hideKeyboard()
            }
            false
        }
    }

    sealed class ReadyToLogin(val isEnable: Boolean,
                              val backgroundInt: Int,
                              val textColorInt: Int) {


        data class ReadyAuthEnter(val isClickableReady: Boolean,
                                  val backgroundIntReady: Int,
                                  val textColorIntReady: Int)
            : ReadyToLogin(isClickableReady, backgroundIntReady, textColorIntReady)

        data class NotReadyAuthEnter(val isClickableNotReady: Boolean,
                                     val backgroundIntNotReady: Int,
                                     val textColorIntNotReady: Int)
            : ReadyToLogin(isClickableNotReady, backgroundIntNotReady, textColorIntNotReady)
    }

    companion object {
        private const val TIME_DURATION_DEBOUNCE: Long = 200
        private const val REQUEST_CODE_EMAIL = 11110
        private const val ERROR_EMPTY_EMAIL = " "
    }

}