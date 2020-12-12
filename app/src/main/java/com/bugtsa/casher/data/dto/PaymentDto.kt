package com.bugtsa.casher.data.dto

import com.bugtsa.casher.data.local.database.entity.payment.PaymentEntity

class PaymentDto {

    val id: Long
    val cost: String
    val balance: String
    val date: String
    val time: String
    val category: String

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
                id = LONG_EMPTY_PAYMENT_FIELD,
                cost = STRING_EMPTY_PAYMENT_FIELD,
                balance = STRING_EMPTY_PAYMENT_FIELD,
                date = STRING_EMPTY_PAYMENT_FIELD,
                time = STRING_EMPTY_PAYMENT_FIELD,
                category = STRING_EMPTY_PAYMENT_FIELD
            )
        }

        fun getDateTimePair(rawDate: String): Pair<String, String> {
            val index = rawDate.indexOf(DATE_AND_TIME_DELIMITER)
            val date = rawDate.substring(0, index)
            val time = rawDate.substring(index + DATE_AND_TIME_DELIMITER.length, rawDate.length)
            return date to time
        }

        private const val DATE_AND_TIME_DELIMITER = ","

        private const val LONG_EMPTY_PAYMENT_FIELD = 0L
        const val INT_EMPTY_PAYMENT_FIELD = 0L
        const val STRING_EMPTY_PAYMENT_FIELD = ""
    }
}