package com.bugtsa.casher.ui.screens.main

import com.bugtsa.casher.data.dto.PurchaseDto

interface MainView {
    fun showProgressBar()
    fun hideProgressBar()
    fun setupStatusText(status: String)
    fun setupPurchaseList(purchaseList: MutableList<PurchaseDto>)
    fun startIntent(mLastError: Exception?)
}