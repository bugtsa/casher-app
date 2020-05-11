package com.bugtsa.casher.ui.screens.purchases.show

import com.bugtsa.casher.data.network.payment.PaymentsByDayRes

interface PurchasesView {

    fun showProgressBar(isVisible: Boolean)

    fun showPaymentList(isVisible: Boolean)

    fun setupStatusText(status: String)

    fun setupPurchaseList(paymentsByDayList: List<PaymentsByDayRes>)
    fun scrollToPosition(position: Int)

    fun showBottomScroll()
    fun hideBottomScroll()
}