package com.bugtsa.casher.ui.screens.main

import android.os.AsyncTask
import android.text.TextUtils
import com.bugtsa.casher.arch.models.PurchaseModel
import com.bugtsa.casher.data.LocalCategoryDataStore
import com.bugtsa.casher.data.dto.PurchaseDto
import com.bugtsa.casher.networking.GoogleSheetService
import com.bugtsa.casher.utls.ConstantManager.Companion.DELIMITER_BETWEEN_COLUMNS
import com.bugtsa.casher.utls.ConstantManager.Companion.DELIMITER_BETWEEN_DATE_AND_TIME
import com.bugtsa.casher.utls.ConstantManager.Companion.END_COLUMN_SHEET
import com.bugtsa.casher.utls.ConstantManager.Companion.ROW_START_SHEET
import com.bugtsa.casher.utls.ConstantManager.Companion.START_COLUMN_SHEET
import com.bugtsa.casher.utls.ConstantManager.Companion.TABLE_NAME_SHEET
import com.bugtsa.casher.utls.GoogleSheetManager.Companion.OWN_GOOGLE_SHEET_ID
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.sheets.v4.Sheets
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.IOException
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream
import javax.inject.Inject

class MainPresenter @Inject constructor(googleSheetService: GoogleSheetService) {

    private var serviceSheets: Sheets

    val purchasesList = mutableListOf<PurchaseDto>()

    private var isScrollPurchasesList: Boolean

    @Inject
    lateinit var purchaseModel: PurchaseModel
    @Inject
    lateinit var localCategoryDataStore: LocalCategoryDataStore

    lateinit var mainView: MainView
    private val disposableSubscriptions: CompositeDisposable = CompositeDisposable()


    init {
        this.serviceSheets = googleSheetService.mService
        isScrollPurchasesList = false
    }

    fun onAttachView(landingView: MainView) {
        this.mainView = landingView
    }

    fun onViewDestroy() {
        disposableSubscriptions.dispose()
    }

    fun processData() {
//        addComment("rain")
        MakeRequestTask().execute()
    }

    private fun addComment(category: String) {
        disposableSubscriptions.add(
                localCategoryDataStore.add(category)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ Timber.d("add comment success") },
                                { t -> Timber.e(t, "add comment error") }))
    }

    //region ================= Request Tasks =================

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private inner class MakeRequestTask internal constructor() : AsyncTask<Void, Void, MutableList<PurchaseDto>>() {
        private var mLastError: Exception? = null

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * @return List of names and majors
         * @throws IOException
         */
        private val dataFromApi: MutableList<PurchaseDto>
            @Throws(IOException::class)
            get() {
                val range = TABLE_NAME_SHEET + START_COLUMN_SHEET + ROW_START_SHEET +
                        DELIMITER_BETWEEN_COLUMNS + END_COLUMN_SHEET
                val response = serviceSheets.spreadsheets().values()
                        .get(OWN_GOOGLE_SHEET_ID, range)
                        .execute()
                val values = response.getValues()
                purchaseModel.sizePurchaseList = values.size
                if (values != null) {
                    for (row in values) {
                        var purchase = processPurchaseDto(row[0].toString(), row[1].toString(), row[2].toString())
                        purchasesList.add(purchase)
                    }
                }
                return purchasesList
            }

        fun processPurchaseDto(price: String, dateOfSheet: String, category: String): PurchaseDto {
            when (dateOfSheet.contains(DELIMITER_BETWEEN_DATE_AND_TIME)) {
                true -> {
                    val index = dateOfSheet.indexOf(DELIMITER_BETWEEN_DATE_AND_TIME)
                    val date = dateOfSheet.substring(0, index)
                    val time = dateOfSheet.substring(index + DELIMITER_BETWEEN_DATE_AND_TIME.length, dateOfSheet.length)
                    return PurchaseDto(price, date, time, category)
                }
                false -> return PurchaseDto(price, dateOfSheet, category)
            }
//            if (dateOfSheet.contains(DELIMITER_BETWEEN_DATE_AND_TIME)) {
//                val index = dateOfSheet.indexOf(DELIMITER_BETWEEN_DATE_AND_TIME)
//                val date = dateOfSheet.substring(0, index)
//                val time = dateOfSheet.substring(index + DELIMITER_BETWEEN_DATE_AND_TIME.length, dateOfSheet.length)
//                return PurchaseDto(price, date, time, category)
//            } else {
//                return PurchaseDto(price, dateOfSheet, category)
//            }
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
                mainView.setupPurchaseList(purchaseList, processDateMap(purchaseList))
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

//    private fun processDateMap(purchaseList: MutableList<PurchaseDto>): Map<String, Int> {
////        var dateMap: MutableMap<String, Int> = mutableMapOf()
//
//        return purchaseList
//                .filter { purchase -> !TextUtils.isEmpty(purchase.date) }
////                .flatMap { purchase -> purchase }
////                .filter { purchase -> !dateMap.contains(purchase.date) }
//                .map { purchase ->
//                    //                    if (!dateMap.contains(purchase.date)) {
////                        dateMap.put(purchase.date, purchaseList.indexOf(purchase))
////                    }
//                    return mutableMapOf(Pair(purchase.date, purchase))
//                }
//                .filter { purchase ->  }
////                .filter { purchase ->  }
////                .toMap()
////                .filter {  }
////                .toMap()
////        return dateMap
//    }

    private fun processDateMap(purchaseList: MutableList<PurchaseDto>): MutableMap<String, Int> {
        var dateMap: MutableMap<String, Int> = mutableMapOf()

        purchaseList
                .filter { purchase -> !TextUtils.isEmpty(purchase.date) }
                .map { purchase ->
                    if (!dateMap.contains(purchase.date)) {
                        dateMap.put(purchase.date, purchaseList.indexOf(purchase))
                    }
                }
        return dateMap
    }

    fun requestScrollToDown() {
        mainView.scrollToPosition(purchasesList.size - 1)
    }

    fun checkPositionAdapter(position: Int) {
        if (position <= purchasesList.size - 10 && isScrollPurchasesList()) {
            mainView.showBottomScroll()
        } else {
            mainView.hideBottomScroll()
        }
    }

    //endregion

    //region ================= Scroll Purchases List =================

    private fun isScrollPurchasesList() : Boolean {
        return isScrollPurchasesList
    }

    fun setScrollPurchasesList(isScroll: Boolean) {
        isScrollPurchasesList = isScroll
    }

    //endregion

}