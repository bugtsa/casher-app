package com.bugtsa.casher.ui.screens.main

import android.databinding.DataBindingUtil
import android.os.AsyncTask
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bugtsa.casher.R
import com.bugtsa.casher.data.dto.PurchaseDto
import com.bugtsa.casher.databinding.ControllerMainBinding
import com.bugtsa.casher.ui.activities.RootActivity.Companion.REQUEST_AUTHORIZATION
import com.bugtsa.casher.ui.adapters.PurchaseAdapter
import com.bugtsa.casher.utls.GoogleSheetManager.Companion.OWN_GOOGLE_SHEET_ID
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest
import java.io.IOException

class MainController : Controller() {

    private lateinit var binding: ControllerMainBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        var view: View = inflater.inflate(R.layout.controller_main, container, false)

        binding = DataBindingUtil.bind(view)
        return view
    }

    //region ================= Setup Ui =================

    fun showText(caption : String) {
//        binding.statusTv.text = caption
//        binding.statusTv.visibility = VISIBLE
    }

    //endregion

//    //region ================= Request Tasks =================
//
//    /**
//     * An asynchronous task that handles the Google Sheets API call.
//     * Placing the API calls in their own task ensures the UI stays responsive.
//     */
//    private inner class MakeRequestTask internal constructor(credential: GoogleAccountCredential) : AsyncTask<Void, Void, MutableList<PurchaseDto>>() {
//        private var mService: com.google.api.services.sheets.v4.Sheets? = null
//        private var mLastError: Exception? = null
//
//        /**
//         * Fetch a list of names and majors of students in a sample spreadsheet:
//         * @return List of names and majors
//         * @throws IOException
//         */
//        private val dataFromApi: MutableList<PurchaseDto>
//            @Throws(IOException::class)
//            get() {
////                writePurchase(PurchaseDto("34", "25.12.17", "транспорт. электричка"))
//                val range = "Vova!A1:C"
//                val response = this.mService!!.spreadsheets().values()
//                        .get(OWN_GOOGLE_SHEET_ID, range)
//                        .execute()
//                val values = response.getValues()
//                val purchasesList = mutableListOf<PurchaseDto>()
//                sizePurchaseList = purchasesList.size
//                purchasesList.add(PurchaseDto("Сумма", "Дата", "На что"))
//                if (values != null) {
//                    for (row in values) {
//                        var purchase = PurchaseDto(row[0].toString(), row[1].toString(), row[2].toString())
//                        purchasesList.add(purchase)
//                    }
//                }
//                return purchasesList
//            }
//
//        init {
//            val transport = AndroidHttp.newCompatibleTransport()
//            val jsonFactory = JacksonFactory.getDefaultInstance()
//            mService = com.google.api.services.sheets.v4.Sheets.Builder(
//                    transport, jsonFactory, credential)
//                    .setApplicationName("Google Sheets API Android Quickstart")
//                    .build()
//        }
//
//        private fun writePurchase(purchase: PurchaseDto) {
//            val range = "Vova!A85:C"
//            val data: MutableList<Any> = mutableListOf(purchase.price, purchase.date, purchase.category)
//            val arrayData = mutableListOf(data)
//
//            val valueData: ValueRange = ValueRange()
//                    .setRange("Vova!A85:C85")
//                    .setValues(arrayData)
//                    .setMajorDimension("ROWS")
//            var batchData: BatchUpdateValuesRequest = BatchUpdateValuesRequest()
//                    .setValueInputOption("RAW")
//                    .setData(mutableListOf(valueData))
//            val response = mService!!.spreadsheets().values()
//                    .batchUpdate(OWN_GOOGLE_SHEET_ID, batchData)
//                    .execute()
//
//            if (response != null) {
//
//            }
//        }
//
//        /**
//         * Background task to call Google Sheets API.
//         * @param params no parameters needed for this task.
//         */
//        override fun doInBackground(vararg params: Void): MutableList<PurchaseDto>? {
//            try {
//                return dataFromApi
//            } catch (e: Exception) {
//                mLastError = e
//                cancel(true)
//                return null
//            }
//
//        }
//
//        override fun onPreExecute() {
//            showText("")
//            binding.progressPurchase.visibility = VISIBLE
//        }
//
//        override fun onPostExecute(purchaseList: MutableList<PurchaseDto>?) {
//            binding.progressPurchase.visibility = GONE
//            if (purchaseList == null || purchaseList.isEmpty()) {
//                showText("No results returned.")
//            } else {
//                var linearLayoutManager = LinearLayoutManager(baseContext)
//                var purchaseAdapter = PurchaseAdapter(purchaseList)
//                binding.purchases.layoutManager = linearLayoutManager
//                binding.purchases.adapter = purchaseAdapter
//            }
//        }
//
//        override fun onCancelled() {
//            binding.progressPurchase.visibility = GONE
//            if (mLastError != null) {
//                if (mLastError is GooglePlayServicesAvailabilityIOException) {
//                    showGooglePlayServicesAvailabilityErrorDialog(
//                            (mLastError as GooglePlayServicesAvailabilityIOException)
//                                    .connectionStatusCode)
//                } else if (mLastError is UserRecoverableAuthIOException) {
//                    startActivityForResult(
//                            (mLastError as UserRecoverableAuthIOException).intent,
//                            REQUEST_AUTHORIZATION)
//                } else {
//                    showText("The following error occurred:\n" + mLastError!!.message)
//                }
//            } else {
//                showText("Request cancelled.")
//            }
//        }
//    }
//
//    //endregion


}