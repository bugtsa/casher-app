package com.bugtsa.casher.ui.screens.purchases.add_purchase

import android.text.Editable
import android.widget.EditText
import com.bugtsa.casher.arch.models.PurchaseModel
import com.bugtsa.casher.data.dto.PurchaseDto
import com.bugtsa.casher.networking.GoogleSheetService
import com.bugtsa.casher.utls.GoogleSheetManager.Companion.OWN_GOOGLE_SHEET_ID
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest
import com.google.api.services.sheets.v4.model.ValueRange
import javax.inject.Inject

class AddPurchasePresenter @Inject constructor(googleSheetService: GoogleSheetService) {

    private var credential : GoogleAccountCredential
    lateinit var addPurchaseView: AddPurchaseView

    @Inject lateinit var purhcaseModel : PurchaseModel

    var size : Int = 0

    init {
       credential = googleSheetService.mCredential
    }

    fun onAttachView(addPurchaseView: AddPurchaseView) {
        this.addPurchaseView = addPurchaseView
        size = purhcaseModel.sizePurchaseList
    }

    fun addPurchase(pricePurchase: String, categoryPurchase: String) {
        //                writePurchase(PurchaseDto("34", "25.12.17", "транспорт. электричка"))
        var price = pricePurchase

    }

//    private fun writePurchase(purchase: PurchaseDto) {
//        val range = "Vova!A85:C"
//        val data: MutableList<Any> = mutableListOf(purchase.price, purchase.date, purchase.category)
//        val arrayData = mutableListOf(data)
//
//        val valueData: ValueRange = ValueRange()
//                .setRange("Vova!A85:C85")
//                .setValues(arrayData)
//                .setMajorDimension("ROWS")
//        var batchData: BatchUpdateValuesRequest = BatchUpdateValuesRequest()
//                .setValueInputOption("RAW")
//                .setData(mutableListOf(valueData))
//        val response = mService!!.spreadsheets().values()
//                .batchUpdate(OWN_GOOGLE_SHEET_ID, batchData)
//                .execute()
//
//        if (response != null) {
//
//        }
//    }
}