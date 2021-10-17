package com.bugtsa.casher.data.network.payment

import com.bugtsa.casher.domain.models.PaymentModel
import com.bugtsa.casher.utils.DiffItem
import com.google.gson.annotations.SerializedName

data class PaymentsByDayRes(

        @field:SerializedName("id_payment")
        val id: String,

        @field:SerializedName("date_payment")
        val date: String?,

        @field:SerializedName("payment")
        val payment: PaymentModel?
) : DiffItem {
    override fun getItemId(): String {
        return id
    }

    override fun getDiff(): String {
        return date?.let { it } ?: payment?.let { it.toString() } ?: ""
    }
}