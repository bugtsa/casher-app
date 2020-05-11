package com.bugtsa.casher.data.network.payment

import com.google.gson.annotations.SerializedName

data class PaymentPageRes(
        @field:SerializedName("hasWarning")
        val hasWarning: Boolean,

        @field:SerializedName("warning")
        val warningsList: List<PaymentPageWarningsRes>,

        @field:SerializedName("page")
        val page: List<PaymentsByDayRes>
)