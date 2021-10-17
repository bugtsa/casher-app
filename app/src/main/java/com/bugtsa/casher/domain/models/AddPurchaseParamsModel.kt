package com.bugtsa.casher.domain.models

data class AddPurchaseParamsModel(
    val userId: String,
    val price: String,
    val nameCategory: String,
    val date: String
)