package com.bugtsa.casher.data.models.charts

import com.bugtsa.casher.networking.CasherApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChooseChartsModel @Inject constructor(private val casherApi: CasherApi){

    init {
        instance = this
    }

    fun getRangeMonths() = casherApi.getRangeMonths()

    companion object {
        private var instance: ChooseChartsModel? = null
    }
}