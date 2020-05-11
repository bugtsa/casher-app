package com.bugtsa.casher.data.dto

import com.bugtsa.casher.data.local.database.entity.payment.PaymentEntity

class PaymentDto {

    val id: Long
    val cost: String
    val balance: String
    val date: String
    val time: String
    val category: String

    constructor(id: Long, cost: String, balance: String, date: String, category: String) {
        this.id = id
        this.cost = cost
        this.balance = balance
        this.date = date
        this.time = EMPTY_PAYMENT_FIELD
        this.category = category
    }

    constructor(id: Long, cost: String, balance: String, date: String, time: String, category: String) {
        this.id = id
        this.cost = cost
        this.balance = balance
        this.date = date
        this.time = time
        this.category = category
    }

    constructor(paymentEntity: PaymentEntity) {
        this.id = paymentEntity.id
        this.cost = paymentEntity.cost
        this.balance = paymentEntity.balance
        this.date = paymentEntity.date
        this.time = paymentEntity.time
        this.category = paymentEntity.category
    }

    override fun toString(): String {
        return "id: $id \n" +
                "cost: $cost \n" +
                "date: $date \n" +
                "time: $time \n" +
                "category: $category \n" +
                "balance: $balance"
    }

    companion object {
        fun paymentEmptyDto(): PaymentDto {
            return PaymentDto(
                id = 0L,
                cost = EMPTY_PAYMENT_FIELD,
                balance = EMPTY_PAYMENT_FIELD,
                date = EMPTY_PAYMENT_FIELD,
                category = EMPTY_PAYMENT_FIELD
            )
        }

        private const val EMPTY_PAYMENT_FIELD = ""
    }

}