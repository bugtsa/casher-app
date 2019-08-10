package com.bugtsa.casher.ui.screens.purchases.show

import com.bugtsa.casher.data.dto.PaymentDto

interface PurchasesView {

    fun showProgressBar()
    fun hideProgressBar()

    fun setupStatusText(status: String)

    fun startIntent(mLastError: Exception?)

    fun setupPurchaseList(paymentList: MutableList<PaymentDto>,
                          dateMap: MutableMap<String, Int>)
    fun scrollToPosition(position: Int)

    fun showBottomScroll()
    fun hideBottomScroll()
}