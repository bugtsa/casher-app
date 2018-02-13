package com.bugtsa.casher.ui.screens.purchases.add_purchase

import com.bugtsa.casher.arch.models.PurchaseModel
import com.bugtsa.casher.data.dto.PurchaseDto
import com.bugtsa.casher.networking.GoogleSheetService
import com.bugtsa.casher.utils.ConstantManager.Companion.END_COLUMN_SHEET
import com.bugtsa.casher.utils.ConstantManager.Companion.START_COLUMN_SHEET
import com.bugtsa.casher.utils.ConstantManager.Companion.PURCHASE_TABLE_NAME_SHEET
import com.bugtsa.casher.utils.GoogleSheetManager.Companion.OWN_GOOGLE_SHEET_ID
import com.bugtsa.casher.utils.SoftwareUtils
import com.bugtsa.casher.utils.SoftwareUtils.Companion.getCurrentTimeStamp
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse
import com.google.api.services.sheets.v4.model.ValueRange
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import com.bugtsa.casher.data.LocalCategoryDataStore
import com.bugtsa.casher.utils.ParentConstantManager.Companion.DELIMITER_BETWEEN_COLUMNS
import com.bugtsa.casher.utils.ParentConstantManager.Companion.MAJOR_DIMENSION
import com.bugtsa.casher.utils.ParentConstantManager.Companion.VALUE_INPUT_OPTION
import com.maxproj.calendarpicker.Models.YearMonthDay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.ObservableSource
import io.reactivex.annotations.NonNull

import timber.log.Timber
import java.util.concurrent.TimeUnit
import io.reactivex.subjects.PublishSubject


class AddPurchasePresenter @Inject constructor(googleSheetService: GoogleSheetService,
                                               compositeDisposable: CompositeDisposable) {

    private var serviceSheets: Sheets
    private var disposableSubscriptions: CompositeDisposable
    lateinit var addPurchaseView: AddPurchaseView

    @Inject
    lateinit var purchaseModel: PurchaseModel

    @Inject
    lateinit var localCategoryDataStore: LocalCategoryDataStore

    var lastNotEmptyRow: Int = 0

    var installDate: String = ""

    init {
        this.serviceSheets = googleSheetService.mService
        this.disposableSubscriptions = compositeDisposable
    }

    //region ================ Base Methods =================

    fun onAttachView(addPurchaseView: AddPurchaseView) {
        this.addPurchaseView = addPurchaseView
        lastNotEmptyRow = purchaseModel.sizePurchaseList
    }

    fun onViewDestroy() {
        disposableSubscriptions.dispose()
    }

    //endregion

    //region ================= Categories From Database =================

    fun checkExistCategoriesInDatabase() {
        disposableSubscriptions.add(
                localCategoryDataStore.getCategories()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap { categoryList ->
                            var nameList: MutableList<String> = mutableListOf()
                            for (categoryEntity in categoryList) {
                                nameList.add(categoryEntity.name)
                            }
                            Flowable.fromArray(nameList)
                        }
                        .subscribe({ categoriesList: List<String> ->
                            addPurchaseView.setupCategoriesList(categoriesList)
                            Timber.d("get all categories")
                        },
                                { t -> Timber.e(t, "error at check exist categories " + t) }))
    }

    //endregion


    //region ================= Request to add purchase =================

    fun addPurchase(pricePurchase: String, categoryPurchase: String) {
        addPurchaseView.showProgressBar()
        disposableSubscriptions.add(
                PurchaseSubscriber(serviceSheets,
                        PurchaseDto(pricePurchase,
                                SoftwareUtils.timeStampToString(getCurrentTimeStamp(), Locale.getDefault()),
                                categoryPurchase))!!
                        .subscribe(this::onBatchPurchasesCollected,
                                this::onBatchPurchasesCollectionFailure))
    }

    //endregion

//    public fun requestToSearch(searchView: SearchView) {
//        RxSearchObservable.fromView(searchView)
//                .debounce(300, TimeUnit.MILLISECONDS)
//                .filter { text ->
//                    !text.isEmpty()
//                }
//                .distinctUntilChanged()
//                .switchMap { query -> dataFromNetwork(query) }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(Consumer<String> { result -> addPurchaseView.setSearchText(result), throws -> {} })
//    }

    private fun dataFromNetwork(query: String): ObservableSource<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //region ================= CategoryEntity Subscriber =================

    private fun PurchaseSubscriber(service: Sheets, purchase: PurchaseDto): Single<BatchUpdateValuesResponse>? {
        val data: MutableList<Any> = mutableListOf(purchase.price, purchase.time, purchase.category)
        val arrayData = mutableListOf(data)
        purchaseModel.sizePurchaseList = lastNotEmptyRow + 1
        lastNotEmptyRow = purchaseModel.sizePurchaseList
        val valueData: ValueRange = ValueRange()
                .setRange(PURCHASE_TABLE_NAME_SHEET + START_COLUMN_SHEET + lastNotEmptyRow + DELIMITER_BETWEEN_COLUMNS + END_COLUMN_SHEET + lastNotEmptyRow)
                .setValues(arrayData)
                .setMajorDimension(MAJOR_DIMENSION)
        var batchData: BatchUpdateValuesRequest = BatchUpdateValuesRequest()
                .setValueInputOption(VALUE_INPUT_OPTION)
                .setData(mutableListOf(valueData))

        return Single.just("")
                .subscribeOn(Schedulers.newThread())
                .flatMap { emptyString ->
                    Single.just(service!!.spreadsheets().values()
                            .batchUpdate(OWN_GOOGLE_SHEET_ID, batchData)
                            .execute())
                }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun onBatchPurchasesCollected(batchUpdateValuesRes: BatchUpdateValuesResponse) {
        if (!batchUpdateValuesRes.isEmpty()) {
            addPurchaseView.hideProgressBar()
            addPurchaseView.completedAddPurchase()
        }
    }

    fun onBatchPurchasesCollectionFailure(throwable: Throwable) {
    }


    private fun refreshCurrentDate() {
        disposableSubscriptions.add(Flowable
                .interval(10, TimeUnit.SECONDS)
                .flatMap { t ->
                    Flowable.just(
                            SoftwareUtils.modernTimeStampToString(getCurrentTimeStamp(), Locale.getDefault()))
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result -> addPurchaseView.setupCurrentDate(result) }, { err -> }))
    }

    @NonNull
    private val updateSubject = PublishSubject.create<String>()

    private fun loadCurrentDate() {
        disposableSubscriptions.add(Flowable
                .just(SoftwareUtils.modernTimeStampToString(getCurrentTimeStamp(), Locale.getDefault()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .repeatWhen({ repeatHandler ->
                    repeatHandler
                            .flatMap({ result -> updateSubject.toFlowable(BackpressureStrategy.LATEST) })
                })
                .subscribe({ result -> addPurchaseView.setupCurrentDate(result) }, { err -> }))
    }

    fun setupCurrentDate() {
        addPurchaseView.setupCurrentDate(SoftwareUtils.modernTimeStampToString(getCurrentTimeStamp(), Locale.getDefault()))
        refreshCurrentDate()
    }

    fun checkShowDateAndTimePickers(checked: Boolean) {
        if (checked) {
            addPurchaseView.setupDatePicker()
            disposableSubscriptions.clear()
        } else {
            setupCurrentDate()
        }
    }

    fun changeCalendar(selectedDate: YearMonthDay) {
        installDate = "" + String.format("%02d", selectedDate.day) + "." +
                String.format("%02d", selectedDate.month) + "." +
                selectedDate.year
                        .toString()
                        .substring(selectedDate.year.toString().length - 2)
        addPurchaseView.setupTimePicker()
    }

    fun changeTime(hourString: String, minuteString: String) {
        addPurchaseView.setupChangedDate(installDate, hourString +":" + minuteString)
    }

    //endregion

}