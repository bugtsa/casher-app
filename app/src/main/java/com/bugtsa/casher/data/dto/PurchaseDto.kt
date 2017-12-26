package com.bugtsa.casher.data.dto

class PurchaseDto(price: String, date: String, category: String) {

    val id: String = hashCode().toString()
    val price: String = price
    val date: String = date
    val category: String = category
}