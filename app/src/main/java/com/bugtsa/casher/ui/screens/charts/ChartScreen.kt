package com.bugtsa.casher.ui.screens.charts

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bugtsa.casher.R
import com.bugtsa.casher.presentation.chart.ChartViewModel
import com.bugtsa.casher.presentation.chart.ChartViewModelFactory
import com.bugtsa.casher.presentation.chart.DateRange
import pro.horovodovodo4ka.bones.Phalanx
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.ui.FragmentSibling
import pro.horovodovodo4ka.bones.ui.delegates.Page
import toothpick.Toothpick

class ChartScreen(chartPreference: ChartPreference) : Phalanx() {

    override val seed = { ChartFragment() }

    var chartPreference: ChartPreference? = chartPreference
        set(value) {
            field = value
            notifyChange()
        }
}

@SuppressLint("MissingSuperCall")
class ChartFragment : Fragment(),
        BonePersisterInterface<ChartScreen>,
        FragmentSibling<ChartScreen> by Page() {

    private lateinit var chartViewModel: ChartViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_chart, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chartViewModelFactory: ChartViewModelFactory = Toothpick
                .openScopes(requireActivity(), this)
                .getInstance(ChartViewModelFactory::class.java)
        chartViewModel = ViewModelProvider(this, chartViewModelFactory)[ChartViewModel::class.java]

        bone.chartPreference?.also { preference ->
            chartViewModel.requestChartData(preference)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super<BonePersisterInterface>.onSaveInstanceState(outState)
        super<androidx.fragment.app.Fragment>.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<BonePersisterInterface>.onCreate(savedInstanceState)
        super<androidx.fragment.app.Fragment>.onCreate(savedInstanceState)
    }

}

data class ChartPreference(val startDate: DateRange,
                           val endDate: DateRange,
                           val sortMode: Int)