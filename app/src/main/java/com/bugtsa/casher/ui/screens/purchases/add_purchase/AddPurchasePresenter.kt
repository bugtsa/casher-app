package com.bugtsa.casher.ui.screens.purchases.add_purchase

import com.bugtsa.casher.arch.models.PurchaseModel
import com.bugtsa.casher.data.dto.PurchaseDto
import com.bugtsa.casher.networking.GoogleSheetService
import com.bugtsa.casher.utls.ConstantManager.Companion.DELIMITER_BETWEEN_COLUMNS
import com.bugtsa.casher.utls.ConstantManager.Companion.END_COLUMN_SHEET
import com.bugtsa.casher.utls.ConstantManager.Companion.MAJOR_DIMENSION
import com.bugtsa.casher.utls.ConstantManager.Companion.START_COLUMN_SHEET
import com.bugtsa.casher.utls.ConstantManager.Companion.TABLE_NAME_SHEET
import com.bugtsa.casher.utls.ConstantManager.Companion.VALUE_INPUT_OPTION
import com.bugtsa.casher.utls.GoogleSheetManager.Companion.OWN_GOOGLE_SHEET_ID
import com.bugtsa.casher.utls.SoftwareUtils
import com.bugtsa.casher.utls.SoftwareUtils.Companion.getCurrentTimeStamp
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
import com.bugtsa.casher.model.CategoryEntity
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Flowable.fromIterable
import io.reactivex.Observable.fromIterable
import io.reactivex.ObservableSource
import io.reactivex.annotations.NonNull

import timber.log.Timber
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors.toList
import io.reactivex.subjects.PublishSubject
import javax.xml.datatype.DatatypeConstants.SECONDS


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
                .setRange(TABLE_NAME_SHEET + START_COLUMN_SHEET + lastNotEmptyRow + DELIMITER_BETWEEN_COLUMNS + END_COLUMN_SHEET + lastNotEmptyRow)
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

//    fun setupCurrentDate() {
//        disposableSubscriptions.add(Flowable
//                .just(SoftwareUtils.modernTimeStampToString(getCurrentTimeStamp(), Locale.getDefault()))
//                .repeat()
//                .debounce(10, TimeUnit.SECONDS)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe { currentDateAndTime -> addPurchaseView.setupCurrentDate(currentDateAndTime) })
////                addPurchaseView . setupCurrentDate (SoftwareUtils.modernTimeStampToString(getCurrentTimeStamp(), Locale.getDefault()))
//    }

    @NonNull
    private val updateSubject = PublishSubject.create<String>()

    private fun loadCurrentDate() {
        disposableSubscriptions.add(Flowable
                .just(SoftwareUtils.modernTimeStampToString(getCurrentTimeStamp(), Locale.getDefault()))
                .debounce(10, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .repeatWhen({ repeatHandler -> repeatHandler.flatMap({ result -> updateSubject.toFlowable(BackpressureStrategy.LATEST) }) })
                .subscribe({ result -> addPurchaseView.setupCurrentDate(result) }, { err -> }))
    }

    fun setupCurrentDate() {
//        updateSubject.onNext(SoftwareUtils.modernTimeStampToString(getCurrentTimeStamp(), Locale.getDefault()))
        updateSubject.onNext(null.toString())
//        loadCurrentDate()
//        disposableSubscriptions.add(updateSubject.subscribe({ result -> addPurchaseView.setupCurrentDate(result)}))
    }

    //endregion

}