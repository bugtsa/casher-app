package com.bugtsa.casher.ui.screens.purchases.show

import android.text.TextUtils
import com.bugtsa.casher.data.dto.CategoryDto
import com.bugtsa.casher.data.dto.PaymentDto
import com.bugtsa.casher.data.local.database.entity.category.CategoryDataStore
import com.bugtsa.casher.data.models.PurchaseModel
import com.bugtsa.casher.di.inject.PreferenceProvider
import com.bugtsa.casher.utils.ParentConstantManager.Companion.DELIMITER_BETWEEN_DATE_AND_TIME
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class PurchasesPresenter @Inject constructor(preferenceProvider: PreferenceProvider,
                                             injectPurchaseModel: PurchaseModel,
                                             injectCategoryDataStore: CategoryDataStore) {

    private var purchasesModel: PurchaseModel = injectPurchaseModel
    private var categoryDataStore: CategoryDataStore = injectCategoryDataStore

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
        getPurchasesList()
    }

    //region ================= Compare Storage and Network Categories =================

    private fun performCheckStorageCategoriesList() {
        Flowable
                .combineLatest(categoryDataStore.getCategoriesList(), purchasesModel.getCategoriesList(),
                        BiFunction<List<CategoryDto>, List<CategoryDto>, Unit> { local, remote ->
                            checkNetworkCategoriesListInDatabase(local, remote)
                        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ t -> Timber.d("PurchasesPresenter", "verify at check exist categories $t") },
                        { t -> Timber.e("PurchasesPresenter", "error at check exist categories $t") })
                .also { bag.add(it) }
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
        categoryDataStore.add(category)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Timber.d("add categories to database success") },
                        { t -> Timber.e(t, "add categories to database error") })
                .also { bag.add(it) }
    }

    //endregion

    //region ================= Replace Task to Rx functions =================

    private fun getPurchasesList() {
        purchasesModel.getPaymentsList()
                .subscribeOn(Schedulers.newThread())
                .map { purchases ->
                    val newPurchases = mutableListOf<PaymentDto>()
                    purchases.forEach { purchase -> newPurchases.add(processPurchaseDto(purchase)) }
                    newPurchases
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ purchases ->
                    if (purchases.isEmpty()) {
                        purchasesView.setupStatusText("No results returned.")
                    } else {
                        purchasesView.setupPurchaseList(purchases, processDateMap(purchases))
                    }
                }, { t -> Timber.e(t, "getPurchasesList") })
                .also { bag.add(it) }
    }

    private fun processPurchaseDto(oldPayment: PaymentDto): PaymentDto {
        val rawDate = oldPayment.date
        return when (rawDate.contains(DELIMITER_BETWEEN_DATE_AND_TIME)) {
            true -> {
                val index = rawDate.indexOf(DELIMITER_BETWEEN_DATE_AND_TIME)
                val date = rawDate.substring(0, index)
                val time = rawDate.substring(index + DELIMITER_BETWEEN_DATE_AND_TIME.length, rawDate.length)
                PaymentDto(id = oldPayment.id, cost = oldPayment.cost, balance = oldPayment.balance,
                        date = date, time = time, category = oldPayment.category)
            }
            false -> PaymentDto(oldPayment.id , oldPayment.cost, oldPayment.balance, rawDate, oldPayment.category)
        }
    }

    private fun processDateMap(paymentList: MutableList<PaymentDto>): MutableMap<String, Int> {
        val dateMap: MutableMap<String, Int> = mutableMapOf()

        paymentList
                .filter { purchase -> !TextUtils.isEmpty(purchase.date) }
                .map { purchase ->
                    if (!dateMap.contains(purchase.date)) {
                        dateMap[purchase.date] = paymentList.indexOf(purchase)
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

