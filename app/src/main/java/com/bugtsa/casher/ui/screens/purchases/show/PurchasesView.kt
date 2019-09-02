package com.bugtsa.casher.ui.screens.purchases.show

import com.bugtsa.casher.data.dto.PaymentsByDayRes

interface PurchasesView {

    fun showProgressBar()
    fun hideProgressBar()

    fun setupStatusText(status: String)

    fun startIntent(mLastError: Exception?)

    fun setupPurchaseList(paymentsByDayList: List<PaymentsByDayRes>)
    fun scrollToPosition(position: Int)

    fun showBottomScroll()
    fun hideBottomScroll()
}