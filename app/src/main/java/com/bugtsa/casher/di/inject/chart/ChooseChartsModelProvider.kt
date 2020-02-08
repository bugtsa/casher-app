package com.bugtsa.casher.di.inject.chart

import com.bugtsa.casher.data.models.charts.ChooseChartsModel
import com.bugtsa.casher.networking.CasherApi
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ChooseChartsModelProvider(private val casherApi: CasherApi) : Provider<ChooseChartsModel> {
    override fun get(): ChooseChartsModel {
        return ChooseChartsModel(casherApi)
    }
}