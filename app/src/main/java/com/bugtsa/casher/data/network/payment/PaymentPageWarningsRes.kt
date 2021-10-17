package com.bugtsa.casher.data.network.payment

import com.bugtsa.casher.domain.models.PaymentModel
import com.google.gson.annotations.SerializedName

data class PaymentPageWarningsRes(
    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("warnings")
    val warning: PaymentModel
) {

    override fun toString(): String {
        return title + warning
    }
}