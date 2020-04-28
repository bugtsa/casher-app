package com.bugtsa.casher.di.repositories.chart

import com.bugtsa.casher.data.models.charts.ChartModel
import com.bugtsa.casher.networking.CasherApi
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ChartRepositoryProvider(private val casherApi: CasherApi): Provider<ChartModel> {

    override fun get(): ChartModel = ChartModel(casherApi)
}