package com.bugtsa.casher.di.inject.chart

import com.bugtsa.casher.data.models.charts.ChartModel
import com.bugtsa.casher.networking.CasherApi
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ChartModelProvider(private val casherApi: CasherApi): Provider<ChartModel> {

    override fun get(): ChartModel = ChartModel(casherApi)
}