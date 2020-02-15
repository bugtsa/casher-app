package com.bugtsa.casher.di.inject.chart

import com.bugtsa.casher.data.models.charts.BarChartModel
import com.bugtsa.casher.networking.CasherApi
import javax.inject.Provider

class BarChartModelProvider(private val casherApi: CasherApi) : Provider<BarChartModel> {

    override fun get(): BarChartModel = BarChartModel(casherApi)
}