package com.bugtsa.casher.ui.screens.main

import com.bugtsa.casher.data.dto.PurchaseDto

interface MainView {

    fun showProgressBar()
    fun hideProgressBar()

    fun setupStatusText(status: String)

    fun startIntent(mLastError: Exception?)

    fun setupPurchaseList(purchaseList: MutableList<PurchaseDto>,
                          dateMap: MutableMap<String, Int>)
    fun scrollToPosition(position: Int)

    fun showBottomScroll()
    fun hideBottomScroll()
}