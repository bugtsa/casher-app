package com.bugtsa.casher.presentation.chart

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bugtsa.casher.data.models.charts.ChartModel
import com.bugtsa.casher.data.network.chart.ChartDataRes
import com.bugtsa.casher.global.ErrorHandler
import com.bugtsa.casher.global.rx.SchedulersProvider
import com.bugtsa.casher.presentation.optional.RxViewModel
import com.bugtsa.casher.ui.screens.charts.ChartPreference
import toothpick.Toothpick
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChartViewModelFactory @Inject constructor(private val app: Application) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return Toothpick.openScope(app).getInstance(modelClass) as T
    }
}

class ChartViewModel @Inject constructor(private val chartModel: ChartModel) : RxViewModel() {

    private val chartDataLiveData = MutableLiveData<ChartDataRes>()

    fun requestChartData(preference: ChartPreference) = chartModel.requestDataForChart(preference)
            .subscribeOn(SchedulersProvider.io())
            .observeOn(SchedulersProvider.ui())
            .subscribe({res ->
                chartDataLiveData.value = res
            }, ErrorHandler::handle)
            .also(::addDispose)
}