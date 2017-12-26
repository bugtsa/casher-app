package com.bugtsa.casher

import android.Manifest
import android.os.Bundle
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import android.text.TextUtils
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import android.os.AsyncTask
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.ConnectionResult
import android.net.ConnectivityManager
import pub.devrel.easypermissions.EasyPermissions
import android.accounts.AccountManager
import android.content.Intent
import pub.devrel.easypermissions.AfterPermissionGranted
import com.google.api.client.util.ExponentialBackOff
import android.app.ProgressDialog
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.api.services.sheets.v4.SheetsScopes
import android.app.Activity
import android.app.ProgressDialog.show
import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.bugtsa.casher.data.dto.PurchaseDto
import com.bugtsa.casher.databinding.ActivityMainBinding
import com.bugtsa.casher.utls.GoogleSheetManager.Companion.OWN_GOOGLE_SHEET_ID
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest
import com.google.api.services.sheets.v4.model.ValueRange
import java.io.IOException
import java.util.*


class MainActivity : Activity(), EasyPermissions.PermissionCallbacks {
    private lateinit var mCredential: GoogleAccountCredential
    private lateinit var binding: ActivityMainBinding

//    private var mOutputText: TextView? = null
//    private var mCallApiButton: Button? = null
//    internal lateinit var mProgress: ProgressDialog

    //region ================= Implements Methods =================

    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        getResultsFromApi()

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                applicationContext, SCOPES)
                .setBackOff(ExponentialBackOff())
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
        if (!isGooglePlayServicesAvailable) {
            acquireGooglePlayServices()
        } else if (mCredential.selectedAccountName == null) {
            chooseAccount()
        } else if (!isDeviceOnline) {
            binding.statusTv!!.text = "No network connection available."
        } else {
            MakeRequestTask(mCredential).execute()
        }
    }

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
                binding.statusTv!!.text = "This app requires Google Play Services. Please install " + "Google Play Services on your device and relaunch this app."
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
                    mCredential.selectedAccountName = accountName
                    getResultsFromApi()
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
                this@MainActivity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES)
        dialog.show()
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private inner class MakeRequestTask internal constructor(credential: GoogleAccountCredential) : AsyncTask<Void, Void, List<String>>() {
        private var mService: com.google.api.services.sheets.v4.Sheets? = null
        private var mLastError: Exception? = null

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * @return List of names and majors
         * @throws IOException
         */
        private val dataFromApi: List<String>
            @Throws(IOException::class)
            get() {
//                writePurchase(PurchaseDto("34", "25.12.17", "транспорт. электричка"))
                val range = "Vova!A2:C"
                val results = ArrayList<String>()
                val response = this.mService!!.spreadsheets().values()
                        .get(OWN_GOOGLE_SHEET_ID, range)
                        .execute()
                val values = response.getValues()
                val purchasesList: MutableList<PurchaseDto> = MutableList( PurchaseDto("Сумма", "Дата", "На что"))
                if (values != null) {
                    for (row in values) {
                        var purchase: PurchaseDto = PurchaseDto(row[0].toString(), row[1].toString(), row[2].toString())
                        results.add(purchase.price + ", " + purchase.date + ", " + purchase.category)
                        purchasesList.add(purchase)
                    }
                }
                return results
            }

        init {
            val transport = AndroidHttp.newCompatibleTransport()
            val jsonFactory = JacksonFactory.getDefaultInstance()
            mService = com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API Android Quickstart")
                    .build()
        }

        private fun writePurchase(purchase: PurchaseDto) {
            val range = "Vova!A85:C"
            val data: MutableList<Any> = mutableListOf(purchase.price, purchase.date, purchase.category)
            val arrayData = mutableListOf(data)

            val valueData : ValueRange = ValueRange()
                    .setRange("Vova!A85:C85")
                    .setValues(arrayData)
                    .setMajorDimension("ROWS")
            var batchData : BatchUpdateValuesRequest = BatchUpdateValuesRequest()
                    .setValueInputOption("RAW")
                    .setData(mutableListOf(valueData))
            val response = mService!!.spreadsheets().values()
                    .batchUpdate(OWN_GOOGLE_SHEET_ID, batchData)
                    .execute()

            if (response != null) {

            }
        }

        /**
         * Background task to call Google Sheets API.
         * @param params no parameters needed for this task.
         */
        override fun doInBackground(vararg params: Void): List<String>? {
            try {
                return dataFromApi
            } catch (e: Exception) {
                mLastError = e
                cancel(true)
                return null
            }

        }


        override fun onPreExecute() {
            binding.statusTv!!.text = ""
            binding.progressPurchase.show()
        }

        override fun onPostExecute(output: List<String>?) {
            binding.progressPurchase.hide()
            if (output == null || output.size == 0) {
                binding.statusTv!!.text = "No results returned."
            } else {
                output.toMutableList().add(0, "Data retrieved using the Google Sheets API:")
                binding.statusTv!!.text = TextUtils.join("\n", output)
            }
        }

        override fun onCancelled() {
            binding.progressPurchase.hide()
            if (mLastError != null) {
                if (mLastError is GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            (mLastError as GooglePlayServicesAvailabilityIOException)
                                    .connectionStatusCode)
                } else if (mLastError is UserRecoverableAuthIOException) {
                    startActivityForResult(
                            (mLastError as UserRecoverableAuthIOException).intent,
                            MainActivity.REQUEST_AUTHORIZATION)
                } else {
                    binding.statusTv!!.text = "The following error occurred:\n" + mLastError!!.message
                }
            } else {
                binding.statusTv!!.text = "Request cancelled."
            }
        }
    }

    companion object {

        internal val REQUEST_ACCOUNT_PICKER = 1000
        internal val REQUEST_AUTHORIZATION = 1001
        internal val REQUEST_GOOGLE_PLAY_SERVICES = 1002
        internal const val REQUEST_PERMISSION_GET_ACCOUNTS = 1003

        private val BUTTON_TEXT = "Call Google Sheets API"
        private val PREF_ACCOUNT_NAME = "accountName"
        private val SCOPES = mutableListOf(SheetsScopes.DRIVE)
    }

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
