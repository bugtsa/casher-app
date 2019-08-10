package com.bugtsa.casher.data.dto

import com.bugtsa.casher.data.local.database.entity.payment.PaymentEntity

class PaymentDto {

    val id: Long
    val price: String
    val date: String
    val time: String
    val category: String

    constructor(id: Long, price: String, date: String, category: String) {
        this.id = id
        this.price = price
        this.date = date
        this.time = ""
        this.category = category
    }

    constructor(id: Long, price: String, date: String, time: String, category: String) {
        this.id = id
        this.price = price
        this.date = date
        this.time = time
        this.category = category
    }

    constructor(paymentEntity: PaymentEntity) {
        this.id = paymentEntity.id
        this.price = paymentEntity.price
        this.date = paymentEntity.date
        this.time = paymentEntity.time
        this.category = paymentEntity.category
    }

}