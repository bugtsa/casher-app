package com.bugtsa.casher.data.dto

class PurchaseDto {

    val id: String
    val price: String
    val date: String
    val time: String
    val category: String

    constructor(price: String, date: String, category: String) {
        id = hashCode().toString()
        this.price = price
        this.date = ""
        this.time = date
        this.category = category
    }

    constructor(price: String, date: String, time: String, category: String) {
        id = hashCode().toString()
        this.price = price
        this.date = date
        this.time = time
        this.category = category
    }

}