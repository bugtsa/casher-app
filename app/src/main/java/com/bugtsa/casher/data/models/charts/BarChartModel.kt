package com.bugtsa.casher.data.models.charts

import com.bugtsa.casher.data.network.chart.ChartDataRes
import com.bugtsa.casher.networking.CasherApi
import com.bugtsa.casher.ui.screens.charts.ChartPreference
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BarChartModel @Inject constructor(private val casherApi: CasherApi) {

    init {
        instance = this
    }

    fun requestDataForChart(preference: ChartPreference): Single<ChartDataRes> {
        val map = hashMapOf<String, String>()
        map[ChartModel.userIdChartParameter] = "0"
        map[ChartModel.monthChartParameter] = preference.endDate.month.toString()
        map[ChartModel.yearChartParameter] = preference.endDate.year.toString()
        map[ChartModel.sortModeChartParameter] = preference.sortMode.toString()
        return casherApi.getChartData(map)
    }

    companion object {
        private var  instance: BarChartModel? = null
    }
}