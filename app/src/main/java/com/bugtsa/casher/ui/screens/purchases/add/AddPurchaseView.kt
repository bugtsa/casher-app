package com.bugtsa.casher.ui.screens.purchases.add

interface AddPurchaseView {
    fun completedAddPurchase()

    fun showProgressBar()
    fun hideProgressBar()

    fun setupCategoriesList(categoriesList: List<String>)

    fun setupCurrentDateAndTime(dateAndTime: String)
    fun showDatePicker()
}