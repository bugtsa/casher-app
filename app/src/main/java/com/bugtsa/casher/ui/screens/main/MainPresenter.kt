package com.bugtsa.casher.ui.screens.main

import android.os.AsyncTask
import com.bugtsa.casher.arch.models.PurchaseModel
import com.bugtsa.casher.data.dto.PurchaseDto
import com.bugtsa.casher.networking.GoogleSheetService
import com.bugtsa.casher.utls.GoogleSheetManager.Companion.OWN_GOOGLE_SHEET_ID
import com.bugtsa.casher.utls.GoogleSheetManager.Companion.SHEET_RANGE
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.sheets.v4.Sheets
import io.reactivex.disposables.CompositeDisposable
import java.io.IOException
import javax.inject.Inject

class MainPresenter @Inject constructor(googleSheetService : GoogleSheetService) {

    private var credential: GoogleAccountCredential
    private var serviceSheets: Sheets

    @Inject lateinit var purchaseModel : PurchaseModel

    lateinit var mainView: MainView
    val disposableSubscriptions: CompositeDisposable = CompositeDisposable()

    init {
        this.credential = googleSheetService.mCredential
        this.serviceSheets = googleSheetService.mService
    }

    fun onAttachView(landingView: MainView) {
        this.mainView = landingView
    }

    fun processData() {
        MakeRequestTask(credential, serviceSheets).execute()
    }

    //region ================= Request Tasks =================

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private inner class MakeRequestTask internal constructor(credentialGoogle: GoogleAccountCredential,
                                                             googleSheetService: Sheets) : AsyncTask<Void, Void, MutableList<PurchaseDto>>() {
//        private var mService: com.google.api.services.sheets.v4.Sheets? = null
        private var mLastError: Exception? = null

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * @return List of names and majors
         * @throws IOException
         */
        private val dataFromApi: MutableList<PurchaseDto>
            @Throws(IOException::class)
            get() {
                val range = SHEET_RANGE
                val response = serviceSheets!!.spreadsheets().values()
                        .get(OWN_GOOGLE_SHEET_ID, range)
                        .execute()
                val values = response.getValues()
                val purchasesList = mutableListOf<PurchaseDto>()
                purchaseModel.sizePurchaseList = values.size
                if (values != null) {
                    for (row in values) {
                        var purchase = PurchaseDto(row[0].toString(), row[1].toString(), row[2].toString())
                        purchasesList.add(purchase)
                    }
                }
                return purchasesList
            }

        /**
         * Background task to call Google Sheets API.
         * @param params no parameters needed for this task.
         */
        override fun doInBackground(vararg params: Void): MutableList<PurchaseDto>? {
            try {
                return dataFromApi
            } catch (e: Exception) {
                mLastError = e
                cancel(true)
                return null
            }

        }

        override fun onPreExecute() {
            mainView.showProgressBar()
        }

        override fun onPostExecute(purchaseList: MutableList<PurchaseDto>?) {
            mainView.hideProgressBar()
            if (purchaseList == null || purchaseList.isEmpty()) {
                mainView.setupStatusText("No results returned.")
            } else {
                mainView.setupPurchaseList(purchaseList)
            }
        }

        override fun onCancelled() {
            mainView.hideProgressBar()
            if (mLastError != null) {
                if (mLastError is GooglePlayServicesAvailabilityIOException) {
//                    showGooglePlayServicesAvailabilityErrorDialog(
//                            (mLastError as GooglePlayServicesAvailabilityIOException)
//                                    .connectionStatusCode)
                } else if (mLastError is UserRecoverableAuthIOException) {
                    mainView.startIntent(mLastError)
                } else {
                    mainView.setupStatusText("The following error occurred:\n" + mLastError!!.message)
                }
            } else {
                mainView.setupStatusText("Request cancelled.")
            }
        }
    }

    //endregion

}