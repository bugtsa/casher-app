package com.bugtsa.casher.ui.screens.singIn

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bugtsa.casher.R
import com.bugtsa.casher.ui.activities.MainActivity
import com.bugtsa.casher.ui.screens.purchases.show.PurchasesScreen
import com.bugtsa.casher.ui.screens.settings.NavigationStackPresentable
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.common.AccountPicker
import com.google.android.gms.common.GoogleApiAvailability
import pro.horovodovodo4ka.bones.Phalanx
import pro.horovodovodo4ka.bones.extensions.present
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.ui.FragmentSibling
import pro.horovodovodo4ka.bones.ui.delegates.Page
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class SingUpScreen : Phalanx(), NavigationStackPresentable {
    override val seed = { SingUpFragment() }

    override val fragmentTitle: String
        get() = ""
}

@SuppressLint("MissingSuperCall")
class SingUpFragment : Fragment(),
        BonePersisterInterface<SingUpScreen>,
        FragmentSibling<SingUpScreen> by Page(),
        SingUpView {

    @Inject
    lateinit var presenter: SingUpPresenter

    private lateinit var singUpScreenScope: Scope

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_choose_charts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        singUpScreenScope = Toothpick.openScopes(activity, this)
        Toothpick.inject(this, singUpScreenScope)

        presenter.onAttachView(this)
        presenter.requestAccountName()
//        test_label.text = getString(R.string.sing_up_in_app)

        refreshUI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super<BonePersisterInterface>.onSaveInstanceState(outState)
        super<androidx.fragment.app.Fragment>.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<BonePersisterInterface>.onCreate(savedInstanceState)
        super<androidx.fragment.app.Fragment>.onCreate(savedInstanceState)
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

    override fun showMainController() {
        bone.present(PurchasesScreen())
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

}