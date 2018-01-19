package com.bugtsa.casher.ui.screens.purchases.add_purchase

interface AddPurchaseView {
    fun completedAddPurchase()

    fun showProgressBar()
    fun hideProgressBar()

    fun setSearchText(result: String)
}