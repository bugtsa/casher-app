package com.bugtsa.casher.presentation

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bugtsa.casher.data.dto.CategoryDto
import com.bugtsa.casher.data.local.database.entity.category.CategoryDataStore
import com.bugtsa.casher.data.models.PurchaseModel
import com.bugtsa.casher.presentation.optional.RxViewModel
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
import toothpick.Toothpick
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddPurchaseViewModelFactory @Inject constructor(
        private val app: Application
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return Toothpick.openScope(app).getInstance(modelClass) as T
    }
}

class AddPurchaseViewModel @Inject constructor(
        compositeDisposable: CompositeDisposable,
        injectPurchaseModel: PurchaseModel,
        injectCategoryDataStore: CategoryDataStore
) : RxViewModel() {

    private var purchaseModel: PurchaseModel = injectPurchaseModel
    private var categoryDataStore: CategoryDataStore = injectCategoryDataStore

    private var customDate: String = ""
    private var customTime: String = ""
    private var checkedCustomDateTime: Boolean = false

    private val categoriesListLiveData = MutableLiveData<List<String>>()
    fun observeCategoriesList(): LiveData<List<String>> = categoriesListLiveData

    private val showProgressLiveData = MutableLiveData<Boolean>()
    fun observeShowProgress(): LiveData<Boolean> = showProgressLiveData

    private val showDatePickerLiveData = MutableLiveData<Boolean>()
    fun observeShowDatePicker(): LiveData<Boolean> = showDatePickerLiveData

    private val showTimePickerLiveData = MutableLiveData<Boolean>()
    fun observeShowTimePicker(): LiveData<Boolean> = showTimePickerLiveData

    private val setupCurrentDateLiveData = MutableLiveData<String>()
    fun observeSetupCurrentDate(): LiveData<String> = setupCurrentDateLiveData

    private val completeAddPaymentLiveData = MutableLiveData<Boolean>()
    fun observeCompleteAddPayment(): LiveData<Boolean> = completeAddPaymentLiveData

    //region ================ Base Methods =================

    fun onViewDestroy() {
        onCleared()
    }

    //endregion

    //region ================= Categories From Database =================

    fun checkExistCategoriesInDatabase() {
        categoryDataStore.getCategoriesList()
                .subscribeOn(Schedulers.io())
                .map { list -> list.map { category -> category.name } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ categoriesList: List<String> ->
                    categoriesListLiveData.value = categoriesList
                    Timber.d("get all categories")
                }, { t -> Timber.e(t, "error at check exist categories $t") })
                .also(::addDispose)
    }

    //endregion

    //region ================= Request to add purchase =================

    private fun addPurchase(pricePurchase: String, nameCategory: String) {
        showProgressLiveData.value = true
        val partFormBody = FormBody.Builder()
                .add(USER_ID_PARAMETER, DEFAULT_USER_ID)
                .add(COST_PARAMETER, pricePurchase)
                .add(CATEGORY_PARAMETER, nameCategory)
                .add(DATE_PARAMETER, getActualDateAndTime())
                .build()
        purchaseModel.addPayment(partFormBody)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ payment ->
                    showProgressLiveData.value = false
                    payment?.also {
                        completeAddPayment()
                    }
                }, {
                    showProgressLiveData.value = false
                    completeAddPayment()
                })
                .also(::addDispose)
    }

    //endregion

    //region ================= Add & Check Server Categories List =================

    fun checkCategorySaveOnDatabase(pricePayment: String, nameCategory: String) {
        categoryDataStore.getCategoriesList()
                .subscribeOn(Schedulers.io())
                .map { list ->
                    list.mapNotNull { it.name }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ storageCategoriesList: List<String> ->
                    if (!isContainsCurrentCategoryInDatabase(nameCategory, storageCategoriesList)) {
                        addCategoryToServer(pricePayment, nameCategory)
                    } else {
                        addPurchase(pricePayment, nameCategory)
                    }
                },
                        { t -> Timber.e(t, "error at check exist categories") })
                .also(::addDispose)
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
                .also(::addDispose)
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
                .also(::addDispose)
    }

    //endregion

    private fun completeAddPayment() {
        showProgressLiveData.value = false
        completeAddPaymentLiveData.value = true
    }

    //region ================= Setup Current Date =================

    fun requestSetupCurrentDate() {
        val date = SoftwareUtils.modernTimeStampToString(getCurrentTimeStamp(), Locale.getDefault())
        setupCurrentDate(date)
    }

    private fun setupCurrentDate(date: String) {
        setupCurrentDateLiveData.value = date
        refreshCurrentDate()
    }


    private fun refreshCurrentDate() {
        Flowable
                .interval(10, TimeUnit.SECONDS)
                .flatMap {
                    Flowable.just(
                            SoftwareUtils.modernTimeStampToString(getCurrentTimeStamp(), Locale.getDefault()))
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result -> setupCurrentDate(result) }, { _ -> })
                .also(::addDispose)
    }

    fun checkSetupCustomDateAndTime(checkedCustomDateTime: Boolean) {
        this.checkedCustomDateTime = checkedCustomDateTime
        if (checkedCustomDateTime) {
            showDatePickerLiveData.value = true
            onCleared()
        } else {
            requestSetupCurrentDate()
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
        showTimePickerLiveData.value = true
    }

    fun changeTime(hourString: String, minuteString: String) {
        customTime = "$hourString:$minuteString"
        setupCurrentDateLiveData.value = "$customDate $customTime"
    }

    //endregion
}