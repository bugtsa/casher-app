package com.bugtsa.casher.presentation.chart

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bugtsa.casher.data.models.charts.BarChartModel
import com.bugtsa.casher.global.ErrorHandler
import com.bugtsa.casher.global.rx.SchedulersProvider
import com.bugtsa.casher.presentation.optional.RxViewModel
import com.bugtsa.casher.ui.screens.charts.ChartPreference
import toothpick.Toothpick
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BarChartViewModelFactory @Inject constructor(private val application: Application) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return Toothpick.openScope(application).getInstance(modelClass) as T
    }
}

class BarChartViewModel @Inject constructor(private val barChartModel: BarChartModel) : RxViewModel() {

    private lateinit var chartData: ChartData

    private val readinessChartLiveData = MutableLiveData<QuantityPortions>()
    fun observeReadinessDataChart(): LiveData<QuantityPortions> = readinessChartLiveData

    private val chartPortionDataLiveData = MutableLiveData<PortionData>()
    fun observeChartPortionData(): LiveData<PortionData> = chartPortionDataLiveData

    fun requestChartData(preference: ChartPreference) = barChartModel.requestDataForChart(preference)
            .subscribeOn(SchedulersProvider.io())
            .observeOn(SchedulersProvider.ui())
            .subscribe({ res ->
                res.first().also { data ->
                    val dateRange  = data.requestMonth?.let { month ->
                        data.requestYear?.let { year ->
                            UiStringDateRange(UiDateRange(DateRange(month.toInt(), year.toInt())))
                        }
                    } ?: UiStringDateRange(DefaultDateRange())
                    val list = data.categorizedMap?.let { map ->
                         map.asIterable().toList()
                    } ?: mutableListOf()
                    chartData = ChartData(dateRange, list)
                    readinessChartLiveData.value = QuantityPortions(list.size)
                }
            }, ErrorHandler::handle)
            .also(::addDispose)

    fun requestPortion(page: Int) {
        val list = chartData.list
                .asIterable()
                .drop(page * portionSize)
                .take(portionSize)
                .map { (key, value) ->
                    key to value
                }
        chartPortionDataLiveData.value = PortionData(chartData.date, PageNumber(page), list)
    }

    companion object {
        const val portionSize = 4
    }
}

class QuantityPortions(private val barDataSize: Int) {
    val value get() = barDataSize / BarChartViewModel.portionSize
}

class PageNumber(private val page: Int) {
    val value: Int get() = page + 1
}

data class ChartData(val date: UiStringDateRange,
                     val list: List<Map.Entry<String, String>>)

data class PortionData(val requestDate: UiStringDateRange,
                       val pageNumber: PageNumber,
                       val portion: List<Pair<String, String>>)