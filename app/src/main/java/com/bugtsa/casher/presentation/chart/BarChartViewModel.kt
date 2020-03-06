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

    private var barData = mutableListOf<Map.Entry<String, String>>()

    private val quantityPortionsLiveData = MutableLiveData<Int>()
    fun observeQuantityPortions(): LiveData<Int> = quantityPortionsLiveData

    private val chartDataLiveData = MutableLiveData<BarPortionData>()
    fun observeChartData(): LiveData<BarPortionData> = chartDataLiveData

    fun requestChartData(preference: ChartPreference) = barChartModel.requestDataForChart(preference)
            .subscribeOn(SchedulersProvider.io())
            .observeOn(SchedulersProvider.ui())
            .subscribe({ res ->
                res.first().categorizedMap?.also {
                    barData.addAll(it.asIterable().toList())
                    quantityPortionsLiveData.value = barData.size / portionSize
                }
            }, ErrorHandler::handle)
            .also(::addDispose)

    fun requestPortion(page: Int) {
        val list = barData
                .asIterable()
                .drop(page * portionSize)
                .take(portionSize)
                .map { (key, value) ->
                    key to value
                }
        chartDataLiveData.value = BarPortionData(list)
    }

    companion object {
        const val portionSize = 4
    }
}

data class BarPortionData(val portion: List<Pair<String, String>>)