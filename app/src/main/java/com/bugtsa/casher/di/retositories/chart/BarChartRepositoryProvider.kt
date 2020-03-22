package com.bugtsa.casher.di.retositories.chart

import com.bugtsa.casher.data.models.charts.BarChartModel
import com.bugtsa.casher.networking.CasherApi
import javax.inject.Provider

class BarChartRepositoryProvider(private val casherApi: CasherApi) : Provider<BarChartModel> {

    override fun get(): BarChartModel = BarChartModel(casherApi)
}