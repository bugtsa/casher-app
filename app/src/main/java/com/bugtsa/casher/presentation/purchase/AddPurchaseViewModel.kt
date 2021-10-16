package com.bugtsa.casher.presentation.purchase

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bugtsa.casher.data.dto.CategoryDto
import com.bugtsa.casher.data.dto.PaymentDto
import com.bugtsa.casher.data.dto.PaymentDto.Companion.INT_EMPTY_PAYMENT_FIELD
import com.bugtsa.casher.data.dto.PaymentDto.Companion.STRING_EMPTY_PAYMENT_FIELD
import com.bugtsa.casher.data.dto.PaymentDto.Companion.getDateTimePair
import com.bugtsa.casher.data.local.database.entity.category.CategoryDataStore
import com.bugtsa.casher.data.local.database.entity.payment.PaymentDataStore
import com.bugtsa.casher.data.models.PurchaseRemoteRepository
import com.bugtsa.casher.presentation.optional.RxViewModel
import com.bugtsa.casher.utils.ConstantManager.CategoryNetwork.NAME_CATEGORY_PARAMETER
import com.bugtsa.casher.utils.ConstantManager.Network.CATEGORY_PARAMETER
import com.bugtsa.casher.utils.ConstantManager.Network.COST_PARAMETER
import com.bugtsa.casher.utils.ConstantManager.Network.DATE_PARAMETER
import com.bugtsa.casher.utils.ConstantManager.Network.USER_ID_PARAMETER
import com.bugtsa.casher.utils.ConstantManager.User.DEFAULT_USER_ID
import com.bugtsa.casher.utils.SoftwareUtils
import com.bugtsa.casher.utils.SoftwareUtils.Companion.getCurrentTimeStamp
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
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
    injectPurchaseRepository: PurchaseRemoteRepository,
    injectCategoryDataStore: CategoryDataStore,
    injectPaymentDataStore: PaymentDataStore
) : RxViewModel() {

    private val remotePurchaseRepo: PurchaseRemoteRepository = injectPurchaseRepository
    private val categoryDataStore: CategoryDataStore = injectCategoryDataStore
    private val localPaymentRepo: PaymentDataStore = injectPaymentDataStore

    private var checkedCustomDateTime: Boolean = false

    private val categoriesListLiveData = MutableLiveData<List<String>>()
    fun observeCategoriesList(): LiveData<List<String>> = categoriesListLiveData

    private val showProgressLiveData = MutableLiveData<Boolean>()
    fun observeShowProgress(): LiveData<Boolean> = showProgressLiveData

    private val showDatePickerLiveData = MutableLiveData<Boolean>()
    fun observeShowDatePicker(): LiveData<Boolean> = showDatePickerLiveData

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
        remotePurchaseRepo.addPayment(partFormBody)
            .flatMap { payment ->
                val (date, time) = payment.date?.let {
                    payment.time?.let {
                        payment.date to it
                    } ?: getDateTimePair(payment.date)

                } ?: STRING_EMPTY_PAYMENT_FIELD to STRING_EMPTY_PAYMENT_FIELD
                localPaymentRepo.add(
                    PaymentDto(
                        id = payment.id ?: INT_EMPTY_PAYMENT_FIELD,
                        cost = payment.cost ?: STRING_EMPTY_PAYMENT_FIELD,
                        balance = payment.balance ?: STRING_EMPTY_PAYMENT_FIELD,
                        date = date,
                        time = time,
                        category = payment.category ?: STRING_EMPTY_PAYMENT_FIELD,
                        categoryId = payment.categoryId ?: INT_EMPTY_PAYMENT_FIELD
                    )
                )
            }
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

    private fun isContainsCurrentCategoryInDatabase(
        currentCategory: String,
        storageCategoriesList: List<String>
    ): Boolean {
        if (storageCategoriesList.isNotEmpty()) {
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

        remotePurchaseRepo.addCategory(categoryFormBody)
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
                    SoftwareUtils.modernTimeStampToString(getCurrentTimeStamp(), Locale.getDefault())
                )
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result -> setupCurrentDate(result) }, { Timber.e("could not refresh current date") })
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
        return if (checkedCustomDateTime && setupCurrentDateLiveData.value != null) {
            setupCurrentDateLiveData.value.toString()
        } else {
            SoftwareUtils.timeStampToString(getCurrentTimeStamp(), Locale.getDefault())
        }
    }

    fun changeDate(dateSetup: String) {
        setupCurrentDateLiveData.value = dateSetup
    }

    //endregion
}