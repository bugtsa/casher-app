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
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse
import com.google.api.services.sheets.v4.model.ValueRange
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AddPurchasePresenter @Inject constructor(googleSheetService: GoogleSheetService,
                                               compositeDisposable: CompositeDisposable) {

    private var serviceSheets: Sheets
    private var disposableSubscriptions: CompositeDisposable
    lateinit var addPurchaseView: AddPurchaseView

    @Inject lateinit var purchaseModel: PurchaseModel

    var lastNotEmptyRow: Int = 0

    init {
        this.serviceSheets = googleSheetService.mService
        this.disposableSubscriptions = compositeDisposable
    }

    fun onAttachView(addPurchaseView: AddPurchaseView) {
        this.addPurchaseView = addPurchaseView
        lastNotEmptyRow = purchaseModel.sizePurchaseList
    }

    fun onViewDestroy() {
        disposableSubscriptions.dispose()
    }

    fun addPurchase(pricePurchase: String, categoryPurchase: String) {
        addPurchaseView.showProgressBar()
        disposableSubscriptions.add(
                writePurchase(serviceSheets,
                        PurchaseDto(pricePurchase, "25.12.17", categoryPurchase))!!
                        .subscribe(this::onBatchPurchasesCollected,
                                this::onBatchPurchasesCollectionFailure))
    }

    fun onTimerEnded(unit: Long) {
        addPurchaseView.hideProgressBar()
        addPurchaseView.completedAddPurchase()
    }

    private fun startTimerForApplyUpdates(batchUpdateValuesRes: BatchUpdateValuesResponse) {
        Single.timer(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTimerEnded,
                        this::onBatchPurchasesCollectionFailure)
    }

    fun onBatchPurchasesCollected(batchUpdateValuesRes: BatchUpdateValuesResponse) {
        if (batchUpdateValuesRes != null) {
            startTimerForApplyUpdates(batchUpdateValuesRes)
        }
    }

    fun onBatchPurchasesCollectionFailure(throwable: Throwable) {
    }

    private fun writePurchase(service: Sheets, purchase: PurchaseDto): Single<BatchUpdateValuesResponse>? {
        val data: MutableList<Any> = mutableListOf(purchase.price, purchase.date, purchase.category)
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
}