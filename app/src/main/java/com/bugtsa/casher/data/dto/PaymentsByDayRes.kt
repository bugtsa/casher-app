package com.bugtsa.casher.data.dto

import com.google.gson.annotations.SerializedName

data class PaymentsByDayRes(

        @field:SerializedName("date_payment")
        val date: String?,

        @field:SerializedName("payment")
        val payment: PaymentDto?
)