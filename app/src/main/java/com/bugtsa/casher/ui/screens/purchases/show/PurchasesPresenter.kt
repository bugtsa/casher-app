package com.bugtsa.casher.ui.screens.purchases.show

import android.text.TextUtils
import com.bugtsa.casher.data.dto.CategoryDto
import com.bugtsa.casher.data.dto.PurchaseDto
import com.bugtsa.casher.data.models.PurchaseModel
import com.bugtsa.casher.di.inject.PreferenceProvider
import com.bugtsa.casher.domain.local.database.LocalCategoryDataStore
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
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class PurchasesPresenter @Inject constructor(preferenceProvider: PreferenceProvider,
                                             injectPurchaseModel: PurchaseModel,
                                             injectLocalCategoryDataStore: LocalCategoryDataStore) {

    private var purchasesModel: PurchaseModel = injectPurchaseModel
    private var localCategoryDataStore: LocalCategoryDataStore = injectLocalCategoryDataStore

    private var isScrollPurchasesList: Boolean

    private val bag: CompositeDisposable = CompositeDisposable()
    private lateinit var purchasesView: PurchasesView

    init {
        isScrollPurchasesList = false
    }

    fun onAttachView(landingView: PurchasesView) {
        this.purchasesView = landingView
    }

    fun onViewDestroy() {
        bag.dispose()
    }

    fun processData() {
        performCheckStorageCategoriesList()
//        getPurchasesList()
    }

    //region ================= Compare Storage and Network Categories =================

    private fun performCheckStorageCategoriesList() {
        bag.add(Flowable
                .combineLatest(localCategoryDataStore.getCategoriesList(), purchasesModel.getCategoriesList(),
                        BiFunction<List<CategoryDto>, List<CategoryDto>, Unit> { local, remote ->
                            checkNetworkCategoriesListInDatabase(local, remote)
                        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ t -> Timber.d("PurchasesPresenter", "verify at check exist categories $t") },
                        { t -> Timber.e("PurchasesPresenter", "error at check exist categories $t") }))
    }

    private fun checkNetworkCategoriesListInDatabase(storageCategoriesList: List<CategoryDto>,
                                                     networkCategoriesList: List<CategoryDto>) {
        val isListsHasSameContent: Boolean = networkCategoriesList sameContentWith storageCategoriesList ?: false

        if (networkCategoriesList.isNotEmpty() && !isListsHasSameContent) {
            networkCategoriesList
                    .forEach { networkCategory ->
                        if (!storageCategoriesList.equalRemoteCategory(networkCategory)) {
                            addCategoryToDatabase(networkCategory)
                        }
                    }
        }
    }

    private infix fun <T> Collection<T>.sameContentWith(collection: Collection<T>?) = collection?.let { this.size == it.size && this.containsAll(it) }

    private fun Collection<CategoryDto>.equalRemoteCategory(remoteCategory: CategoryDto): Boolean {
        this.forEach {
            if (it == remoteCategory) {
                return true
            }
        }
        return false
    }

    //endregion

    //region ================= DataBase =================

    private fun addCategoryToDatabase(category: CategoryDto) {
        bag.add(
                localCategoryDataStore.add(category)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ Timber.d("add categories to database success") },
                                { t -> Timber.e(t, "add categories to database error") }))
    }

    //endregion

    //region ================= Replace Task to Rx functions =================

    private fun getPurchasesList() {

    }

    private fun getPurchasesList(service: Sheets) {
        val range = PURCHASE_TABLE_NAME_SHEET + START_COLUMN_SHEET + ROW_START_SHEET +
                DELIMITER_BETWEEN_COLUMNS + END_COLUMN_SHEET
        purchasesView.showProgressBar()
        bag.add(Flowable.just("")
                .subscribeOn(Schedulers.newThread())
                .flatMap { _ ->
                    Flowable.just(service.spreadsheets().values()
                            .get(OWN_GOOGLE_SHEET_ID, range)
                            .execute())
                            .map { rawList -> rawList.getValues() }
                            .map { values ->
                                val purchasesList = mutableListOf<PurchaseDto>()
                                purchasesModel.sizePurchaseList = values.size
                                for (row in values) {
                                    val purchase = processPurchaseDto(row[0].toString(), row[1].toString(), row[2].toString())
                                    purchasesList.add(purchase)
                                }
                                purchasesList
                            }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ purchases ->
                    purchasesView.hideProgressBar()
                    if (purchases.isEmpty()) {
                        purchasesView.setupStatusText("No results returned.")
                    } else {
                        purchasesView.setupPurchaseList(purchases, processDateMap(purchases))
                    }
                },
                        { throwable ->
                            purchasesView.hideProgressBar()
                            if (throwable is GooglePlayServicesAvailabilityIOException) {

                            } else if (throwable is UserRecoverableAuthIOException) {
                                purchasesView.startIntent(throwable)
                            } else {
                                purchasesView.setupStatusText("The following error occurred:\n" + throwable.message)
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
        purchasesView.scrollToPosition(purchasesModel.sizePurchaseList - 1)
    }

    fun checkPositionAdapter(position: Int) {
        if (position <= purchasesModel.sizePurchaseList - 10 && isScrollPurchasesList()) {
            purchasesView.showBottomScroll()
        } else {
            purchasesView.hideBottomScroll()
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

