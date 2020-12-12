package com.bugtsa.casher.data.network.payment

data class PaymentRes(
	val date: String? = null,
	val time: String? = null,
	val amount: String? = null,
	val cost: String? = null,
	val balance: String? = null,
	val price: String? = null,
	val description: String? = null,
	val id: Long? = null,
	val category: String? = null,
	val userId: String? = null
)
