package com.bugtsa.casher.presentation.chart

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bugtsa.casher.data.models.charts.BarChartModel
import com.bugtsa.casher.global.ErrorHandler
import com.bugtsa.casher.global.ErrorHandler.handle
import com.bugtsa.casher.global.rx.SchedulersProvider
import com.bugtsa.casher.presentation.optional.RxViewModel
import com.bugtsa.casher.ui.screens.charts.ChartPreference
import toothpick.Toothpick
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PieCharViewModelFactory @Inject constructor(private val application: Application) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return Toothpick.openScope(application).getInstance(modelClass) as T
    }
}

class PieChartViewModel @Inject constructor(private val chartModel: BarChartModel) : RxViewModel() {

    private val readinessChartLiveData = MutableLiveData<PieChartData>()
    fun observeReadinessDataChart(): LiveData<PieChartData> = readinessChartLiveData

    fun requestChartData(preference: ChartPreference) = chartModel.requestDataForChart(preference)
            .subscribeOn(SchedulersProvider.io())
            .observeOn(SchedulersProvider.ui())
            .subscribe({ res ->
                res.first().also { data ->
                    val dateRange = data.requestMonth?.let { month ->
                        data.requestYear?.let { year ->
                            UiStringDateRange(UiDateRange(DateRange(month.toInt(), year.toInt())))
                        }
                    } ?: UiStringDateRange(DefaultDateRange())
                    val list = data.categorizedMap?.let { map ->
                                map.asIterable().toList()
                            }
                            ?.map { it ->
                                it.key to it.value.toFloat()
                            } ?: mutableListOf()
                    readinessChartLiveData.value = PieChartData(dateRange, list)
                }
            }, ErrorHandler::handle)
            .also(::addDispose)

}

data class PieChartData(val date: UiStringDateRange,
                        val listData: List<Pair<String, Float>>)