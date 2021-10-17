package com.bugtsa.casher.domain.interactors

import com.bugtsa.casher.data.dto.AddPurchaseDto
import com.bugtsa.casher.domain.models.PaymentModel
import com.bugtsa.casher.data.local.database.entity.payment.PaymentDataStore
import com.bugtsa.casher.data.prefs.LocalSettingsRepository
import com.bugtsa.casher.data.repositories.PurchaseRemoteRepository
import com.bugtsa.casher.utils.ConstantManager
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddPurchaseInteractor @Inject constructor(
    private val remotePurchaseRepo: PurchaseRemoteRepository,
    private val localPaymentRepo: PaymentDataStore,
    private val prefsRepo: LocalSettingsRepository
) {

    fun addPurchase(
        pricePurchase: String,
        nameCategory: String,
        date: String
    ): Single<PaymentModel> =
        remotePurchaseRepo.addPayment(
            AddPurchaseDto(
                ConstantManager.User.DEFAULT_USER_ID,
                pricePurchase,
                nameCategory,
                date
            )
        )
            .flatMap { payment ->
                prefsRepo.saveCustomDate(date)
                localPaymentRepo.add(payment)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun getCustomDate(): String = prefsRepo.getCustomDate()
}