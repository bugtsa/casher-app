package com.bugtsa.casher.data.network.payment

import com.bugtsa.casher.data.dto.PaymentDto
import com.google.gson.annotations.SerializedName

data class PaymentPageRes(
    @field:SerializedName("hasWarning")
    val hasWarning: Boolean,

    @field:SerializedName("warning")
    val warningsList: List<PaymentPageWarningsRes>,

    @field:SerializedName("page")
    val page: List<PaymentsByDayRes>
) {

    companion object {
        fun returnWarning(titleWarning: String): PaymentPageRes {
            return PaymentPageRes(
                hasWarning = true,
                warningsList = listOf(PaymentPageWarningsRes(titleWarning, PaymentDto.paymentEmptyDto())),
                page = listOf()
            )
        }

        const val NEED_REFRESH_TOKEN = "You must refresh auth token"
    }
}