package com.bugtsa.casher.data.network.payment

import com.bugtsa.casher.domain.models.PaymentModel
import com.google.gson.annotations.SerializedName

data class PaymentRes(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("userId")
    val userId: String? = null,
    @SerializedName("cost")
    val cost: String? = null,
    @SerializedName("date")
    val date: String? = null,
    @SerializedName("time")
    val time: String? = null,
    @SerializedName("category")
    val category: String? = null,
    @SerializedName("categoryId")
    val categoryId: Int? = null,
    @SerializedName("balance")
    val balance: String? = null,
    val amount: String? = null,
    val price: String? = null,
    val description: String? = null
) {

    companion object {

        private const val EMPTY_STRING = ""
        private const val DEFAULT_INT = 0

        fun PaymentRes.toModel(date: String, time: String): PaymentModel =
            PaymentModel(
                id = this.id ?: DEFAULT_INT,
                cost = this.cost ?: EMPTY_STRING,
                balance = this.balance ?: EMPTY_STRING,
                date = date,
                time = time,
                category = this.category ?: EMPTY_STRING,
                categoryId = this.categoryId ?: DEFAULT_INT
            )
    }
}
