package com.bugtsa.casher.ui.screens.charts

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.chart.common.listener.Event
import com.anychart.chart.common.listener.ListenersInterface
import com.anychart.enums.Align
import com.anychart.enums.LegendLayout
import com.bugtsa.casher.R
import com.bugtsa.casher.presentation.chart.PieCharViewModelFactory
import com.bugtsa.casher.presentation.chart.PieChartViewModel
import com.bugtsa.casher.presentation.chart.UiStringDateRange
import kotlinx.android.synthetic.main.fragment_chart_common.*
import pro.horovodovodo4ka.bones.Phalanx
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.ui.FragmentSibling
import pro.horovodovodo4ka.bones.ui.delegates.Page
import toothpick.Toothpick

class PieChartScreen(chartPreference: ChartPreference) : Phalanx() {
    override val seed = { PieChartFragment() }

    var chartPreference: ChartPreference? = chartPreference
        set(value) {
            field = value
            notifyChange()
        }
}

@SuppressLint("MissingSuperCall")
class PieChartFragment : Fragment(),
        BonePersisterInterface<PieChartScreen>,
        FragmentSibling<PieChartScreen> by Page() {

    private lateinit var viewModel: PieChartViewModel

    override fun onSaveInstanceState(outState: Bundle) {
        super<BonePersisterInterface>.onSaveInstanceState(outState)
        super<androidx.fragment.app.Fragment>.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<BonePersisterInterface>.onCreate(savedInstanceState)
        super<androidx.fragment.app.Fragment>.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chart_common, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModelFactory = Toothpick
                .openScopes(requireActivity(), this)
                .getInstance(PieCharViewModelFactory::class.java)
        viewModel = ViewModelProvider(this, viewModelFactory)[PieChartViewModel::class.java]

        bindViewModel()
    }

    private fun bindViewModel() {
        bone.chartPreference?.also { preference ->
            viewModel.requestChartData(preference)
        }

        viewModel.observeReadinessDataChart().observe(viewLifecycleOwner, Observer { pieData ->
            val data: MutableList<DataEntry> = ArrayList()
            pieData.listData.forEach{ (key, value) ->
                data.add(ValueDataEntry(key, value))
            }
            showPieChart(pieData.date, data)
        })
    }

    private fun showPieChart(uiStringDateRange: UiStringDateRange, data: List<DataEntry>) {
        val anyChartView: AnyChartView = vAnyChartView
        anyChartView.setProgressBar(vPieProgressBar)
        val pie = AnyChart.pie()
        pie.setOnClickListener(object : ListenersInterface.OnClickListener(arrayOf("x", "value")) {
            override fun onClick(event: Event) {
                Toast.makeText(requireContext(), event.data["x"].toString() + ":" + event.data["value"], Toast.LENGTH_SHORT).show()
            }
        })
        pie.data(data)
        pie.title(getString(R.string.charts_caption_pie, uiStringDateRange.month, uiStringDateRange.year))
        pie.labels().position("outside")
        pie.legend().title().enabled(true)
        pie.legend().title()
                .text(getString(R.string.charts_caption_legend_pie))
                .padding(0.0, 0.0, 10.0, 0.0)
        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER)
        anyChartView.setChart(pie)
    }
}