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

    fun requestDataForChart(preference: ChartPreference): Single<List<ChartDataRes>> {
        val map = hashMapOf<String, String>()
        map[ChartModel.userIdChartParameter] = userIdDefault
        map[START_MONTH_PARAMETER] = preference.startDate.month.toString()
        map[START_YEAR_PARAMETER] = preference.startDate.year.toString()
        map[END_MONTH_PARAMETER] = preference.endDate.month.toString()
        map[END_YEAR_PARAMETER] = preference.endDate.year.toString()
        map[ChartModel.sortModeChartParameter] = preference.sortMode.toString()
        return casherApi.getRangeMonthChartData(map)
    }

    companion object {
        private var  instance: BarChartModel? = null

        private const val userIdDefault = "0"

        private const val START_MONTH_PARAMETER = "startMonth"
        private const val START_YEAR_PARAMETER = "startYear"
        private const val END_MONTH_PARAMETER = "endMonth"
        private const val END_YEAR_PARAMETER = "endYear"
    }
}