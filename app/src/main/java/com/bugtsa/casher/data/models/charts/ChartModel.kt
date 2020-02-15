package com.bugtsa.casher.data.models.charts

import com.bugtsa.casher.data.network.chart.ChartDataRes
import com.bugtsa.casher.networking.CasherApi
import com.bugtsa.casher.ui.screens.charts.ChartPreference
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChartModel @Inject constructor(private val casherApi: CasherApi) {

    init {
        instance = this
    }

    fun requestDataForChart(preference: ChartPreference): Single<ChartDataRes> {
        val map = hashMapOf<String, String>()
        map[userIdChartParameter] = "0"
        map[monthChartParameter] = preference.endDate.month.toString()
        map[yearChartParameter] = preference.endDate.year.toString()
        map[sortModeChartParameter] = preference.sortMode.toString()
        return casherApi.getChartData(map)
    }

    companion object {
        private var instance: ChartModel? = null

        const val userIdChartParameter = "userId"
        const val monthChartParameter = "month"
        const val yearChartParameter = "year"
        const val sortModeChartParameter = "sort"
    }
}