package com.bugtsa.casher.ui.screens.main

import android.text.TextUtils
import com.bugtsa.casher.arch.models.PurchaseModel
import com.bugtsa.casher.domain.local.database.LocalCategoryDataStore
import com.bugtsa.casher.data.dto.PurchaseDto
import com.bugtsa.casher.di.inject.PreferenceProvider
import com.bugtsa.casher.utils.ConstantManager.Companion.END_COLUMN_SHEET
import com.bugtsa.casher.utils.ConstantManager.Companion.PURCHASE_TABLE_NAME_SHEET
import com.bugtsa.casher.utils.ConstantManager.Companion.ROW_START_SHEET
import com.bugtsa.casher.utils.ConstantManager.Companion.START_COLUMN_SHEET
import com.bugtsa.casher.utils.GoogleSheetManager.Companion.OWN_GOOGLE_SHEET_ID
import com.bugtsa.casher.utils.ParentConstantManager.Companion.DELIMITER_BETWEEN_COLUMNS
import com.bugtsa.casher.utils.ParentConstantManager.Companion.DELIMITER_BETWEEN_DATE_AND_TIME
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.sheets.v4.Sheets
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class MainPresenter @Inject constructor(preferenceProvider: PreferenceProvider,
                                        injectPurchaseModel: PurchaseModel,
                                        injectLocalCategoryDataStore: LocalCategoryDataStore
) {

//    private var serviceSheets: Sheets = preferenceProvider.mService
    private var purchaseModel: PurchaseModel = injectPurchaseModel
    private var localCategoryDataStore: LocalCategoryDataStore = injectLocalCategoryDataStore

    private var isScrollPurchasesList: Boolean

    private val disposableSubscriptions: CompositeDisposable = CompositeDisposable()
    lateinit var mainView: MainView

    init {
        isScrollPurchasesList = false
    }

    fun onAttachView(landingView: MainView) {
        this.mainView = landingView
    }

    fun onViewDestroy() {
        disposableSubscriptions.dispose()
    }

    fun processData() {
//        performCheckStorageCategoriesList()
//        getPurchasesList(serviceSheets)
    }

    //region ================= Compare Storage and Network Categories =================

    private fun performCheckStorageCategoriesList() {
//        disposableSubscriptions.add(
//                localCategoryDataStore.getCategoriesList()
//                        .subscribeOn(Schedulers.io())
//                        .map { it.mapNotNull { it.name } }
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .doOnNext { storageCategoriesList: List<String> ->
//                            disposableSubscriptions.add(getServerCategoriesList(serviceSheets)!!
//                                    .subscribe({ networkCategoriesList ->
//                                        checkNetworkCategoriesListInDatabase(networkCategoriesList, storageCategoriesList)
//                                    },
//                                            { t -> Log.e("MainPresenter", "error at check exist categories $t") }))
//                        }
//                        .subscribe({ _: List<String> ->
//                        },
//                                { t -> Log.e("MainPresenter", "error at check exist categories $t") }))
    }

//    private fun getServerCategoriesList(service: Sheets): Flowable<List<String>>? {
//        val range = CATEGORIES_TABLE_NAME_SHEET + START_COLUMN_SHEET + ROW_START_SHEET
////        +                DELIMITER_BETWEEN_COLUMNS + START_COLUMN_SHEET
//
//        return Flowable.just("")
//                .subscribeOn(Schedulers.newThread())
//                .flatMap { _ ->
//                    Flowable.just(service.spreadsheets().values()
//                            .get(OWN_GOOGLE_SHEET_ID, range)
//                            .execute()
//                            .getValues()
//                            .map { rowList: MutableList<Any>? -> rowList!!.lastOrNull().toString() })
//                }
//                .observeOn(AndroidSchedulers.mainThread())
//    }
//
//    private fun checkNetworkCategoriesListInDatabase(networkCategoriesList: List<String>,
//                                                     storageCategoriesList: List<String>) {
//        val isListsHasSameContent: Boolean? = networkCategoriesList sameContentWith storageCategoriesList
//
//        if (!networkCategoriesList.isEmpty() && !isListsHasSameContent!!) {
//
//            networkCategoriesList
//                    .filter { networkCategory -> !storageCategoriesList.contains(networkCategory) }
//                    .forEach { networkCategory -> addCategoryToDatabase(networkCategory) }
//
////            for (category in networkCategoriesList) {
////                if (!storageCategoriesList.contains(category)) {
////                    addCategoryToDatabase(category)
////                }
////            }
//        }
//    }

    private infix fun <T> Collection<T>.sameContentWith(collection: Collection<T>?) = collection?.let { this.size == it.size && this.containsAll(it) }

    //endregion

    //region ================= DataBase =================

    private fun addCategoryToDatabase(category: String) {
        disposableSubscriptions.add(
                localCategoryDataStore.add(category)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ Timber.d("add categories to database success") },
                                { t -> Timber.e(t, "add categories to database error") }))
    }

    //endregion

    //region ================= Replace Task to Rx functions =================

    private fun getPurchasesList(service: Sheets) {
        val range = PURCHASE_TABLE_NAME_SHEET + START_COLUMN_SHEET + ROW_START_SHEET +
                DELIMITER_BETWEEN_COLUMNS + END_COLUMN_SHEET
        mainView.showProgressBar()
        disposableSubscriptions.add(Flowable.just("")
                .subscribeOn(Schedulers.newThread())
                .flatMap { _ ->
                    Flowable.just(service.spreadsheets().values()
                            .get(OWN_GOOGLE_SHEET_ID, range)
                            .execute())
                            .map { rawList -> rawList.getValues() }
                            .map { values ->
                                val purchasesList = mutableListOf<PurchaseDto>()
                                purchaseModel.sizePurchaseList = values.size
                                for (row in values) {
                                    val purchase = processPurchaseDto(row[0].toString(), row[1].toString(), row[2].toString())
                                    purchasesList.add(purchase)
                                }
                                purchasesList
                            }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ purchases ->
                    mainView.hideProgressBar()
                    if (purchases.isEmpty()) {
                        mainView.setupStatusText("No results returned.")
                    } else {
                        mainView.setupPurchaseList(purchases, processDateMap(purchases))
                    }
                },
                        { throwable ->
                            mainView.hideProgressBar()
                            if (throwable is GooglePlayServicesAvailabilityIOException) {

                            } else if (throwable is UserRecoverableAuthIOException) {
                                mainView.startIntent(throwable)
                            } else {
                                mainView.setupStatusText("The following error occurred:\n" + throwable.message)
                            }
                            Timber.e(throwable, "error at check exist categories")
                        }))
    }

    private fun processPurchaseDto(price: String, dateOfSheet: String, category: String): PurchaseDto {
        when (dateOfSheet.contains(DELIMITER_BETWEEN_DATE_AND_TIME)) {
            true -> {
                val index = dateOfSheet.indexOf(DELIMITER_BETWEEN_DATE_AND_TIME)
                val date = dateOfSheet.substring(0, index)
                val time = dateOfSheet.substring(index + DELIMITER_BETWEEN_DATE_AND_TIME.length, dateOfSheet.length)
                return PurchaseDto(price, date, time, category)
            }
            false -> return PurchaseDto(price, dateOfSheet, category)
        }
    }

    private fun processDateMap(purchaseList: MutableList<PurchaseDto>): MutableMap<String, Int> {
        val dateMap: MutableMap<String, Int> = mutableMapOf()

        purchaseList
                .filter { purchase -> !TextUtils.isEmpty(purchase.date) }
                .map { purchase ->
                    if (!dateMap.contains(purchase.date)) {
                        dateMap.put(purchase.date, purchaseList.indexOf(purchase))
                    }
                }
        return dateMap
    }

    //endregion

    //region ================= Scroll Function =================

    fun requestScrollToDown() {
        mainView.scrollToPosition(purchaseModel.sizePurchaseList - 1)
    }

    fun checkPositionAdapter(position: Int) {
        if (position <= purchaseModel.sizePurchaseList - 10 && isScrollPurchasesList()) {
            mainView.showBottomScroll()
        } else {
            mainView.hideBottomScroll()
        }
    }

    //endregion

    //region ================= Scroll Purchases List =================

    private fun isScrollPurchasesList(): Boolean {
        return isScrollPurchasesList
    }

    fun setScrollPurchasesList(isScroll: Boolean) {
        isScrollPurchasesList = isScroll
    }

    //endregion

}