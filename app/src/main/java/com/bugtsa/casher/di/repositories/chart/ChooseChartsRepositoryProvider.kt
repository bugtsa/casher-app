package com.bugtsa.casher.di.repositories.chart

import com.bugtsa.casher.data.models.charts.ChooseChartsRepository
import com.bugtsa.casher.networking.CasherApi
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ChooseChartsRepositoryProvider(private val casherApi: CasherApi) : Provider<ChooseChartsRepository> {
    override fun get(): ChooseChartsRepository {
        return ChooseChartsRepository(casherApi)
    }
}