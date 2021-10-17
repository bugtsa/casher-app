package com.bugtsa.casher.data.dto

import com.bugtsa.casher.utils.ConstantManager
import okhttp3.FormBody

data class AddPurchaseDto(
    val userId: String,
    val price: String,
    val nameCategory: String,
    val date: String
) {

    companion object {

        fun AddPurchaseDto.toFormBody(): FormBody =
            FormBody.Builder()
                .add(ConstantManager.Network.USER_ID_PARAMETER, userId)
                .add(ConstantManager.Network.COST_PARAMETER, price)
                .add(ConstantManager.Network.CATEGORY_PARAMETER, nameCategory)
                .add(ConstantManager.Network.DATE_PARAMETER, date)
                .build()

    }
}