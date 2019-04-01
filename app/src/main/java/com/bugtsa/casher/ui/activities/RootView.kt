package com.bugtsa.casher.ui.activities

import com.bugtsa.casher.data.dto.PaymentRes
import io.reactivex.Observable
import java.util.List

interface RootView {
    fun getPayments(allPayments: Observable<List<PaymentRes>>)
}