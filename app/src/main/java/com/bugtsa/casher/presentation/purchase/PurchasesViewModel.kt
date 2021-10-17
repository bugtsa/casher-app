package com.bugtsa.casher.presentation.purchase

import android.app.Application
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bugtsa.casher.data.AuthRepository
import com.bugtsa.casher.data.dto.CategoryDto
import com.bugtsa.casher.domain.models.PaymentModel
import com.bugtsa.casher.data.local.database.entity.category.CategoryDataStore
import com.bugtsa.casher.data.local.database.entity.payment.PaymentDataStore
import com.bugtsa.casher.data.repositories.PurchaseRemoteRepository
import com.bugtsa.casher.data.network.payment.PaymentPageRes
import com.bugtsa.casher.data.network.payment.PaymentPageRes.Companion.NEED_REFRESH_TOKEN
import com.bugtsa.casher.data.network.payment.PaymentPageWarningsRes
import com.bugtsa.casher.data.network.payment.PaymentsByDayRes
import com.bugtsa.casher.data.prefs.PreferenceRepository
import com.bugtsa.casher.presentation.optional.RxViewModel
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import toothpick.Toothpick
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.SortedMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PurchasesViewModelFactory @Inject constructor(private val app: Application) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        Toothpick.openScope(app).getInstance(modelClass) as T
}

class PurchasesViewModel @Inject constructor(
    private val purchasesRepository: PurchaseRemoteRepository,
    private val categoryDataStore: CategoryDataStore,
    private val paymentLocalRepo: PaymentDataStore,
    private val preferenceRepo: PreferenceRepository,
    private val authRepository: AuthRepository
) : RxViewModel() {

    private var isScrollPurchasesList: Boolean = false
    private var paymentsListSize: Int? = null

    private val paymentsBag = CompositeDisposable()

    private val progressLiveData = MutableLiveData<Boolean>()
    fun observeProgress(): LiveData<Boolean> = progressLiveData

    private val statusTextLiveData = MutableLiveData<String>()
    fun observeStatusText(): LiveData<String> = statusTextLiveData

    private val setupPurchaseListLiveData = MutableLiveData<List<PaymentsByDayRes>>()
    fun observePurchaseList(): LiveData<List<PaymentsByDayRes>> = setupPurchaseListLiveData

    private val scrollToPositionLiveData = MutableLiveData<Int>()
    fun observeScrollToPosition(): LiveData<Int> = scrollToPositionLiveData

    private val bottomBarVisibilityLiveData = MutableLiveData<Boolean>()
    fun observeBottomBarVisibility(): LiveData<Boolean> = bottomBarVisibilityLiveData

    fun processData() {
        progressLiveData.value = true
        performCheckStorageCategoriesList()
        subscribeOnPayments()
    }

    fun subscribeOnPayments() {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            performCheckStoragePaymentsList()
        }
    }

    fun clearSubscribeOnPayments() {
        paymentsBag.clear()
    }

    fun onAllCleared() {
        onCleared()
        clearSubscribeOnPayments()
    }

    //region ================= Scroll Function =================

    fun requestScrollToDown() {
        paymentsListSize?.also {
            scrollToPositionLiveData.value = it - 1
            setScrollPurchasesList(false)
        }
    }

    fun checkPositionAdapter(position: Int) {
        paymentsListSize?.also {
            bottomBarVisibilityLiveData.value = position <= it - 10 && isScrollPurchasesList()
        }
    }

    //endregion

    //region ================= Compare Storage and Network Categories =================

    private fun performCheckStorageCategoriesList() {
        Flowable
            .combineLatest(categoryDataStore.getCategoriesList(), purchasesRepository.getCategoriesList(),
                BiFunction<List<CategoryDto>, List<CategoryDto>, Unit> { local, remote ->
                    checkNetworkCategoriesListInDatabase(local, remote)
                })
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ t -> Timber.d("verify at check exist categories $t") },
                { t ->
                    progressLiveData.value = false
                    statusTextLiveData.value = "Server not allow, trying later"
                    Timber.e("error at check exist categories $t")
                })
            .also(::addDispose)
    }

    private fun checkNetworkCategoriesListInDatabase(
        storageCategoriesList: List<CategoryDto>,
        networkCategoriesList: List<CategoryDto>
    ) {
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

    private infix fun <T> Collection<T>.sameContentWith(collection: Collection<T>?) =
        collection?.let { this.size == it.size && this.containsAll(it) }

    private fun Collection<CategoryDto>.equalRemoteCategory(remoteCategory: CategoryDto): Boolean {
        this.forEach {
            if (it == remoteCategory) {
                return true
            }
        }
        return false
    }

    //endregion

    @RequiresApi(VERSION_CODES.O)
    private fun performCheckStoragePaymentsList() {
        paymentLocalRepo.getPaymentsList()
            .flatMapSingle { paymentList ->
                if (paymentList.isEmpty()) {
                    getRemotePaymentsByDay()
                        .firstOrError()
                        .flatMap {
                            paymentLocalRepo.saveList(it)
                        }
                } else {
                    Single.fromCallable { processByPageRes(paymentList) }
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ paymentPage ->
                val paymentsList = paymentPage.page
                paymentsListSize = paymentsList.size
                progressLiveData.value = false
                when {
                    paymentPage.hasWarning -> statusTextLiveData.value = processWarningsList(paymentPage.warningsList)
                    paymentsList.isEmpty() -> statusTextLiveData.value = "No results returned."
                    else -> setupPurchaseListLiveData.value = paymentsList
                }
            }, { t ->
                handleError(t)
                progressLiveData.value = false
            })
            .also {
                paymentsBag.add(it)
            }
    }

    @RequiresApi(VERSION_CODES.O)
    private fun processByPageRes(paymentsList: List<PaymentModel>): PaymentPageRes {
        val paymentsPage = (mutableListOf<PaymentPageWarningsRes>() to hashMapOf<String, MutableList<PaymentModel>>())
            .also { (warningsList, paymentsMapByDay) ->
                paymentsList.forEach { payment ->
                    when {
                        payment.date.isEmpty() -> {
                            warningsList.add(PaymentPageWarningsRes("Need setup date for payment", payment))
                        }
                        !paymentsMapByDay.contains(payment.date) -> {
                            val tempPaymentsList = mutableListOf<PaymentModel>()
                            tempPaymentsList.add(payment)
                            paymentsMapByDay[payment.date] = tempPaymentsList
                        }
                        else -> {
                            paymentsMapByDay[payment.date]?.also {
                                it.add(0, payment)
                                paymentsMapByDay[payment.date] = it
                            }
                        }
                    }
                }
            }
        return paymentsPage.let { (warningsList, paymentsMapByDay) ->
            PaymentPageRes(
                hasWarning = warningsList.isNotEmpty(),
                warningsList = warningsList,
                page = mutableListOf<PaymentsByDayRes>()
                    .let { listPaymentsByDay ->
                        getSortedMapPaymentsByDay(paymentsMapByDay).keys.forEach { key ->
                            listPaymentsByDay.add(PaymentsByDayRes(listPaymentsByDay.size.toString(), key, null))
                            paymentsMapByDay[key]?.forEach { payment ->
                                listPaymentsByDay.add(
                                    PaymentsByDayRes(
                                        listPaymentsByDay.size.toString(),
                                        null,
                                        payment
                                    )
                                )
                            }
                        }
                        listPaymentsByDay
                    }
            )
        }
    }

    @RequiresApi(VERSION_CODES.O)
    private fun getSortedMapPaymentsByDay(paymentsByDayMap: HashMap<String, MutableList<PaymentModel>>)
            : SortedMap<String, MutableList<PaymentModel>> = paymentsByDayMap
        .toSortedMap(Comparator { o1, o2 ->
            val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT)
            try {
                LocalDate.parse(o1, formatter).compareTo(LocalDate.parse(o2, formatter))
            } catch (e: DateTimeParseException) {
                throw IllegalArgumentException(e)
            }
        })

    //region ================= DataBase =================

    private fun addCategoryToDatabase(category: CategoryDto) {
        categoryDataStore.add(category)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ Timber.d("add categories to database success") },
                { t -> Timber.e(t, "add categories to database error") })
            .also(::addDispose)
    }

    //endregion

    //region ================= Replace Task to Rx functions =================

    private fun getRemotePaymentsByDay(): Flowable<PaymentPageRes> =
        purchasesRepository.getPaymentsByDay(BEARER_PREFIX + preferenceRepo.getAccessToken())
            .switchMap { page ->
                if (page.hasWarning && page.warningsList.size == 1 && page.warningsList[0].title == NEED_REFRESH_TOKEN) {
                    authRepository.getRefreshedToken(preferenceRepo.getRefreshToken())
                        .toFlowable()
                        .flatMap { authDto ->
                            preferenceRepo.saveAuthData(authDto)
                            purchasesRepository.getPaymentsByDay(BEARER_PREFIX + preferenceRepo.getAccessToken())
                        }
                } else {
                    purchasesRepository.getPaymentsByDay(BEARER_PREFIX + preferenceRepo.getAccessToken())
                }
            }

    private fun processWarningsList(warningsList: List<PaymentPageWarningsRes>): String {
        return when {
            warningsList.size > 1 -> {
                var warnings = ""
                warningsList.forEach { warning -> warnings = warnings.plus(warning.warning.toString()) }
                "Need check all that payments: \n$warnings"
            }
            warningsList.size == 1 -> warningsList[0].toString()
            else -> "Need check warnings list"
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

    companion object {
        private const val BEARER_PREFIX = "Bearer "
        private const val DATE_FORMAT = "dd.MM.yy"
    }
}

