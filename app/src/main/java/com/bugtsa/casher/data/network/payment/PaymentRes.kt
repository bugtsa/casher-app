package com.bugtsa.casher.data.network.payment

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
)
