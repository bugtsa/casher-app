package com.bugtsa.casher.ui.screens.purchases.add

import com.bugtsa.casher.data.dto.CategoryDto
import com.bugtsa.casher.data.local.database.entity.category.CategoryDataStore
import com.bugtsa.casher.data.models.PurchaseModel
import com.bugtsa.casher.utils.ConstantManager.CategoryNetwork.NAME_CATEGORY_PARAMETER
import com.bugtsa.casher.utils.ConstantManager.Network.CATEGORY_PARAMETER
import com.bugtsa.casher.utils.ConstantManager.Network.COST_PARAMETER
import com.bugtsa.casher.utils.ConstantManager.Network.DATE_PARAMETER
import com.bugtsa.casher.utils.ConstantManager.Network.USER_ID_PARAMETER
import com.bugtsa.casher.utils.ConstantManager.User.DEFAULT_USER_ID
import com.bugtsa.casher.utils.SoftwareUtils
import com.bugtsa.casher.utils.SoftwareUtils.Companion.getCurrentTimeStamp
import com.maxproj.calendarpicker.Models.YearMonthDay
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.FormBody
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class AddPurchasePresenter @Inject constructor(compositeDisposable: CompositeDisposable,
                                               injectPurchaseModel: PurchaseModel,
                                               injectCategoryDataStore: CategoryDataStore
) {

    private var bag: CompositeDisposable = compositeDisposable
    private var purchaseModel: PurchaseModel = injectPurchaseModel
    private var categoryDataStore: CategoryDataStore = injectCategoryDataStore

    private var lastNotEmptyPurchaseRow: Int = 0
    private var customDate: String = ""
    private var customTime: String = ""
    private var checkedCustomDateTime: Boolean = false

    private lateinit var addPurchaseView: AddPurchaseView

    //region ================ Base Methods =================

    fun onAttachView(addPurchaseView: AddPurchaseView) {
        this.addPurchaseView = addPurchaseView
        lastNotEmptyPurchaseRow = purchaseModel.sizePurchaseList
    }

    fun onViewDestroy() {
        bag.dispose()
    }

    //endregion

    //region ================= Categories From Database =================

    fun checkExistCategoriesInDatabase() {
        categoryDataStore.getCategoriesList()
                .subscribeOn(Schedulers.io())
                .flatMapIterable { it }
                .map { category -> category.name }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ categoriesList: List<String> ->
                    addPurchaseView.setupCategoriesList(categoriesList)
                    Timber.d("get all categories")
                },
                        { t -> Timber.e(t, "error at check exist categories $t") })
                .also { bag.add(it) }
    }

    //endregion

    //region ================= Request to add purchase =================

    private fun addPurchase(pricePurchase: String, nameCategory: String) {
        addPurchaseView.showProgressBar()
        val partFormBody = FormBody.Builder()
                .add(USER_ID_PARAMETER, DEFAULT_USER_ID)
                .add(COST_PARAMETER, pricePurchase)
                .add(CATEGORY_PARAMETER, nameCategory)
                .add(DATE_PARAMETER, getActualDateAndTime())
                .build()
        purchaseModel.addPayment(partFormBody)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it != null) {
                        completeAddPayment()
                    }
                }, { completeAddPayment() })
                .also { bag.add(it) }
    }

    //endregion

    //region ================= Add & Check Server Categories List =================

    fun checkCategorySaveOnDatabase(pricePayment: String, nameCategory: String) {
        categoryDataStore.getCategoriesList()
                .subscribeOn(Schedulers.io())
                .map { it.mapNotNull { it.name } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ storageCategoriesList: List<String> ->
                    if (!isContainsCurrentCategoryInDatabase(nameCategory, storageCategoriesList)) {
                        addCategoryToServer(pricePayment, nameCategory)
                    } else {
                        addPurchase(pricePayment, nameCategory)
                    }
                },
                        { t -> Timber.e(t, "error at check exist categories") })
                .also { bag.add(it) }
    }

    private fun isContainsCurrentCategoryInDatabase(currentCategory: String, storageCategoriesList: List<String>): Boolean {
        if (!storageCategoriesList.isEmpty()) {
            for (category in storageCategoriesList) {
                if (storageCategoriesList.contains(currentCategory)) {
                    return true
                }
            }
        }
        return false
    }

    private fun addCategoryToDatabase(pricePayment: String, category: CategoryDto) {
        categoryDataStore.add(category)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    addPurchase(pricePayment, category.name)
                    Timber.d("add category to database success")
                },
                        { t -> Timber.e(t, "add category to database error") })
                .also { bag.add(it) }
    }

    private fun addCategoryToServer(pricePayment: String, nameCategory: String) {
        val categoryFormBody = FormBody.Builder()
                .add(NAME_CATEGORY_PARAMETER, nameCategory)
                .build()

        purchaseModel.addCategory(categoryFormBody)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ category ->
                    category?.also { addCategoryToDatabase(pricePayment, it) }
                }, {})
                .also { bag.add(it) }
    }

    //endregion

    private fun completeAddPayment() {
        addPurchaseView.hideProgressBar()
        addPurchaseView.completedAddPurchase()
    }

    //region ================= Setup Current Date =================

    fun setupCurrentDate() {
        addPurchaseView.setupCurrentDateAndTime(SoftwareUtils.modernTimeStampToString(getCurrentTimeStamp(), Locale.getDefault()))
        refreshCurrentDate()
    }


    private fun refreshCurrentDate() {
        bag.add(Flowable
                .interval(10, TimeUnit.SECONDS)
                .flatMap {
                    Flowable.just(
                            SoftwareUtils.modernTimeStampToString(getCurrentTimeStamp(), Locale.getDefault()))
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result -> addPurchaseView.setupCurrentDateAndTime(result) }, { _ -> }))
    }

    fun checkSetupCustomDateAndTime(checkedCustomDateTime: Boolean) {
        this.checkedCustomDateTime = checkedCustomDateTime
        if (checkedCustomDateTime) {
            addPurchaseView.showDatePicker()
            bag.clear()
        } else {
            setupCurrentDate()
        }
    }

    private fun getActualDateAndTime(): String {
        return if (checkedCustomDateTime) {
            "$customDate, $customTime"
        } else {
            SoftwareUtils.timeStampToString(getCurrentTimeStamp(), Locale.getDefault())
        }
    }

    fun changeCalendar(selectedDate: YearMonthDay) {
        customDate = "" + String.format("%02d", selectedDate.day) + "." +
                String.format("%02d", selectedDate.month) + "." +
                selectedDate.year
                        .toString()
                        .substring(selectedDate.year.toString().length - 2)
        addPurchaseView.showTimePicker()
    }

    fun changeTime(hourString: String, minuteString: String) {
        customTime = "$hourString:$minuteString"
        addPurchaseView.setupCustomDateAndTime(customDate, customTime)
    }

    //endregion
}