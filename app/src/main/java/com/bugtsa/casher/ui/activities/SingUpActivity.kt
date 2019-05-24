package com.bugtsa.casher.ui.activities

import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.bugtsa.casher.ui.screens.singIn.SingUpView
import com.crashlytics.android.Crashlytics
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.common.AccountPicker
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_root.*
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.Finger
import pro.horovodovodo4ka.bones.extensions.processBackPress
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.statesstore.EmergencyPersister
import pro.horovodovodo4ka.bones.statesstore.EmergencyPersisterInterface
import pro.horovodovodo4ka.bones.ui.FingerNavigatorInterface
import pro.horovodovodo4ka.bones.ui.delegates.FingerNavigator
import pro.horovodovodo4ka.bones.ui.helpers.ActivityAppRestartCleaner
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject


class RootFinger(root: Bone) : Finger(root) {
	init {
		persistSibling = true
	}
}

@SuppressLint("MissingSuperCall")
class SingUpActivity : AppCompatActivity(), SingUpView,
	FingerNavigatorInterface<RootFinger> by FingerNavigator(android.R.id.content),
	BonePersisterInterface<RootFinger>,
	EmergencyPersisterInterface<SingUpActivity> by EmergencyPersister(), ActivityAppRestartCleaner {
	private lateinit var mCredential: GoogleAccountCredential

	private lateinit var activityScope: Scope
	@Inject
	lateinit var presenter: RootPresenter

	companion object {
		private const val REQUEST_CODE_EMAIL = 1001
		private const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
	}

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

		setContentView(com.bugtsa.casher.R.layout.activity_root)

//		presenter.onAttachView(this)
//		presenter.requestAccountName()
	}

	override fun onStart() {
		super.onStart()
		toolbar.title = getString(com.bugtsa.casher.R.string.app_name)
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

	//region ================= Request Permissions =================

	override fun onActivityResult(
		requestCode: Int, resultCode: Int, data: Intent?
	) {
		super.onActivityResult(requestCode, resultCode, data)
		if (resultCode == RESULT_OK) {
			when (requestCode) {
				REQUEST_GOOGLE_PLAY_SERVICES -> if (resultCode != RESULT_OK) {
					showText("This app requires Google Play Services. Please install " + "Google Play Services on your device and relaunch this app.")
				} else {
					getResultsFromApi(null)
				}
				REQUEST_CODE_EMAIL -> if (data != null && data.extras != null) {
					presenter.saveAccountName(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME))
				}
			}
		}
	}

	//endregion

	override fun showMainController() {
//		if (!emergencyLoad(savedInstanceState, this)) {
//
//			super<ActivityAppRestartCleaner>.onCreate(savedInstanceState)
//
//			bone = RootFinger(MainBone())
//
//			glueWith(bone)
//			bone.isActive = true
//
//			supportFragmentManager
//					.beginTransaction()
//					.replace(android.R.id.content, bone.phalanxes.first().sibling as androidx.fragment.app.Fragment)
//					.commit()
//		} else {
//			glueWith(bone)
//		}
	}

	/**
	 * Attempt to resolve a missing, out-of-date, invalid or disabled Google
	 * Play Services installation via a user dialog, if possible.
	 */
	override fun requestAccountName() {
		val apiAvailability = GoogleApiAvailability.getInstance()
		val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this)
		if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
			showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode)
		} else {
			accountNameIntent()
		}
	}

	//region ================= Request Play Services =================

	private fun accountNameIntent() {
		try {
			val intent = AccountPicker.newChooseAccountIntent(
				null, null,
				arrayOf(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE), false, null, null, null, null
			)
			startActivityForResult(intent, REQUEST_CODE_EMAIL)
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
	internal fun showGooglePlayServicesAvailabilityErrorDialog(
		connectionStatusCode: Int
	) {
		val apiAvailability = GoogleApiAvailability.getInstance()
		val dialog = apiAvailability.getErrorDialog(
			this@SingUpActivity,
			connectionStatusCode,
			REQUEST_GOOGLE_PLAY_SERVICES
		)
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
	private fun getResultsFromApi(savedInstanceState: Bundle?) {
		Log.d("Result", "There are Java developers in Lagos")
	}

	private fun setupAccountNameAndRequestToApi(accountName: String?) {
		mCredential.selectedAccountName = accountName
	}

	//region ================= Root View =================

//	override fun getPayments(allPayments: Observable<java.util.List<PaymentRes>>) {
//		allPayments
//			.subscribeOn(Schedulers.io())
//			.observeOn(AndroidSchedulers.mainThread())
//			.subscribe({ result ->
//				Log.d("Result", "There are ${result.size} Java developers in Lagos")
//			}, { error ->
//				error.printStackTrace()
//			})
//	}

//	fun processLogin(credential: GoogleAccountCredential, savedInstanceState: Bundle?) {
//		mCredential = credential
//		if (!isGooglePlayServicesAvailable) {
//			accountNameIntent()
//		} else if (mCredential.selectedAccountName == null) {
//			chooseAccount()
//		} else if (!isDeviceOnline) {
//			showText("No network connection available.")
//		} else {
////            if (!router.hasRootController()) {
////                router.setRoot(RouterTransaction.with(MainController()))
////            }
//			if (!emergencyLoad(savedInstanceState, this)) {
//
//				super<ActivityAppRestartCleaner>.onCreate(savedInstanceState)
//
//				bone = RootFinger(MainBone())
//
//				glueWith(bone)
//				bone.isActive = true
//
//				supportFragmentManager
//					.beginTransaction()
//					.replace(android.R.id.content, bone.phalanxes.first().sibling as Fragment)
//					.commit()
//			} else {
//				glueWith(bone)
//			}
//
//		}
//	}

	//endregion

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