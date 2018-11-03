package com.bugtsa.casher.ui.activities

import android.Manifest
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View.VISIBLE
import com.bugtsa.casher.R
import com.bugtsa.casher.ui.screens.main.MainBone
import com.crashlytics.android.Crashlytics
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.sheets.v4.SheetsScopes
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_root.*
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.Finger
import pro.horovodovodo4ka.bones.extensions.glueWith
import pro.horovodovodo4ka.bones.extensions.processBackPress
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.statesstore.EmergencyPersister
import pro.horovodovodo4ka.bones.statesstore.EmergencyPersisterInterface
import pro.horovodovodo4ka.bones.ui.FingerNavigatorInterface
import pro.horovodovodo4ka.bones.ui.delegates.FingerNavigator
import pro.horovodovodo4ka.bones.ui.helpers.ActivityAppRestartCleaner
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class RootFinger(root: Bone) : Finger(root) {
    init {
        persistSibling = true
    }
}

class RootActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, RootView,
        FingerNavigatorInterface<RootFinger> by FingerNavigator(android.R.id.content),
        BonePersisterInterface<RootFinger>,
        EmergencyPersisterInterface<RootActivity> by EmergencyPersister(), ActivityAppRestartCleaner {
    private lateinit var mCredential: GoogleAccountCredential

    private lateinit var activityScope: Scope
    @Inject
    lateinit var presenter: RootPresenter

    //region ================= Implements Methods =================

    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super<AppCompatActivity>.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        activityScope = Toothpick.openScopes(application, this)
        Toothpick.inject(this, activityScope)

        setContentView(R.layout.activity_root)

        presenter.onAttachView(this)

        if (!emergencyLoad(savedInstanceState, this)) {

            super<ActivityAppRestartCleaner>.onCreate(savedInstanceState)

            bone = RootFinger(MainBone())

            glueWith(bone)
            bone.isActive = true

            supportFragmentManager
                    .beginTransaction()
                    .replace(android.R.id.content, bone.phalanxes.first().sibling as Fragment)
                    .commit()
        } else {
            glueWith(bone)
        }

        getResultsFromApi()
    }

    override fun onBackPressed() {
        if (bone.processBackPress()) {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        emergencyRemovePin()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super<AppCompatActivity>.onSaveInstanceState(outState)
        emergencyPin(outState)
    }

    //    @SuppressLint("MissingSuperCall")
    override fun onDestroy() {
        super.onDestroy()

        with(bone) {
            sibling = null
            emergencySave {
                it.bone = this
            }
        }
    }

    //endregion

    //region ================= Request Get Account =================

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private fun chooseAccount() {
        if (EasyPermissions.hasPermissions(
                        this, Manifest.permission.GET_ACCOUNTS)) {
            val accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null)
            if (accountName != null) {
                mCredential.selectedAccountName = accountName
                getResultsFromApi()
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER)
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS)
        }
    }

    //endregion

    //region ================= Request Permissions =================


    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     * activity result.
     * @param data Intent (containing result data) returned by incoming
     * activity result.
     */
    override fun onActivityResult(
            requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_GOOGLE_PLAY_SERVICES -> if (resultCode != RESULT_OK) {
                showText("This app requires Google Play Services. Please install " + "Google Play Services on your device and relaunch this app.")
            } else {
                getResultsFromApi()
            }
            REQUEST_ACCOUNT_PICKER -> if (resultCode == RESULT_OK && data != null &&
                    data.extras != null) {
                val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                if (accountName != null) {
                    val settings = getPreferences(Context.MODE_PRIVATE)
                    val editor = settings.edit()
                    editor.putString(PREF_ACCOUNT_NAME, accountName)
                    editor.apply()
                    setupAccountNameAndRequestToApi(accountName)
                }
            }
            REQUEST_AUTHORIZATION -> if (resultCode == RESULT_OK) {
                getResultsFromApi()
            }
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     * requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     * which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this)
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     * permission
     * @param list The requested permission list. Never null.
     */
    override fun onPermissionsGranted(requestCode: Int, list: List<String>) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     * permission
     * @param list The requested permission list. Never null.
     */
    override fun onPermissionsDenied(requestCode: Int, list: List<String>) {
        // Do nothing.
    }

    //endregion

    //region ================= Request Play Services =================

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private fun acquireGooglePlayServices() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode)
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     * Google Play Services on this device.
     */
    internal fun showGooglePlayServicesAvailabilityErrorDialog(
            connectionStatusCode: Int) {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val dialog = apiAvailability.getErrorDialog(
                this@RootActivity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES)
        dialog.show()
    }

    //endregion


    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private fun getResultsFromApi() {
        presenter.requestCredential()
    }

    private fun setupAccountNameAndRequestToApi(accountName: String?) {
        mCredential.selectedAccountName = accountName
        requestToApi(mCredential)
    }

    //region ================= Root View =================

    override fun requestToApi(credential: GoogleAccountCredential) {
        mCredential = credential
        if (!isGooglePlayServicesAvailable) {
            acquireGooglePlayServices()
        } else if (mCredential.selectedAccountName == null) {
            chooseAccount()
        } else if (!isDeviceOnline) {
            showText("No network connection available.")
        } else {
//            if (!router.hasRootController()) {
//                router.setRoot(RouterTransaction.with(MainController()))
//            }
        }
    }

    //endregion

    companion object {

        internal val REQUEST_ACCOUNT_PICKER = 1000
        internal val REQUEST_AUTHORIZATION = 1001
        internal val REQUEST_GOOGLE_PLAY_SERVICES = 1002
        internal const val REQUEST_PERMISSION_GET_ACCOUNTS = 1003

        private val BUTTON_TEXT = "Call Google Sheets API"
        private val PREF_ACCOUNT_NAME = "accountName"
        private val SCOPES = mutableListOf(SheetsScopes.DRIVE)
    }

    //region ================= Setup Ui =================

    private fun showText(caption: String) {
        status_tv.text = caption
        status_tv.visibility = VISIBLE
    }

    //endregion


    //region ================= Utils Methods =================

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private val isDeviceOnline: Boolean
        get() {
            val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private val isGooglePlayServicesAvailable: Boolean
        get() {
            val apiAvailability = GoogleApiAvailability.getInstance()
            val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this)
            return connectionStatusCode == ConnectionResult.SUCCESS
        }

    //endregion

}