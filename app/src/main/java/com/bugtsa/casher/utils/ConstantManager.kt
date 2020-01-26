package com.bugtsa.casher.utils

class ConstantManager {

    object User {
        const val DEFAULT_USER_ID = "0"
    }

    object CategoryNetwork {
        const val CATEGORY_NAME_METHOD = "category"
        const val NAME_CATEGORY_PARAMETER = "name"
    }

    object Network {
        const val PAYMENT_NAME_METHOD = "payment"
        const val PAGE_PAYMENT_NAME_METHOD = "/page"
        const val LAST_PAGE_PAYMENT_NAME_METHOD = "/last"
        const val USER_ID_PARAMETER = "userId"
        const val COST_PARAMETER = "cost"
        const val CATEGORY_PARAMETER = "category"
        const val DATE_PARAMETER = "date"
        const val BALANCE_PARAMETER = "balanceCaption"
        private const val AMOUNT_PARAMETER = "amount"
        private const val PRICE_PARAMETER = "price"
        private const val DESCRIPTION_PARAMETER = "description"
    }

    object Constants {
        const val EMPTY = ""
    }
}