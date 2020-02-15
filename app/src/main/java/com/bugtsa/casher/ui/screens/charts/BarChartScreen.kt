package com.bugtsa.casher.ui.screens.charts

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bugtsa.casher.R
import com.bugtsa.casher.presentation.chart.BarChartViewModel
import com.bugtsa.casher.presentation.chart.BarChartViewModelFactory
import com.bugtsa.casher.presentation.chart.BarPortionData
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.android.synthetic.main.activity_barchart.*
import pro.horovodovodo4ka.bones.Phalanx
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.ui.FragmentSibling
import pro.horovodovodo4ka.bones.ui.delegates.Page
import timber.log.Timber
import toothpick.Toothpick
import java.util.*

class BarChartScreen(chartPreference: ChartPreference) : Phalanx() {
    override val seed = { BarChartFragment() }

    var chartPreference: ChartPreference? = chartPreference
        set(value) {
            field = value
            notifyChange()
        }
}

@SuppressLint("MissingSuperCall")
class BarChartFragment : Fragment(), OnSeekBarChangeListener, OnChartValueSelectedListener,
        BonePersisterInterface<BarChartScreen>,
        FragmentSibling<BarChartScreen> by Page() {

    protected val months = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    )
    protected var tfRegular: Typeface? = null
    @JvmField
    protected var tfLight: Typeface? = null

    private lateinit var viewModel: BarChartViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_barchart, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tfRegular = Typeface.createFromAsset(requireActivity().assets, "OpenSans-Regular.ttf")
        tfLight = Typeface.createFromAsset(requireActivity().assets, "OpenSans-Light.ttf")

        val viewModelFactory = Toothpick
                .openScopes(requireActivity(), this)
                .getInstance(BarChartViewModelFactory::class.java)
        viewModel = ViewModelProvider(this, viewModelFactory)[BarChartViewModel::class.java]

        setupSeekBars()
        setupChart()

        bindViewModel()
    }

    private fun bindViewModel() {
        bone.chartPreference?.also { preference ->
            viewModel.requestChartData(preference)
        }
        viewModel.observeChartData().observe(viewLifecycleOwner, androidx.lifecycle.Observer { chartData ->
            setupNewData(chartData)
        })
    }

    private fun setupNewData(chartData: BarPortionData) {
        Timber.d("charData $chartData")
        showPortion(1, chartData.quantityPortions, chartData.portion)
    }

    private fun setupSeekBars() {
        vSeekBarX.setOnSeekBarChangeListener(this)
        vSeekBarY.setOnSeekBarChangeListener(this)
    }

    private fun setupChart() {
        tvXMax.textSize = 10f
        vChart.setOnChartValueSelectedListener(this)
        vChart.description?.isEnabled = false
        vChart.setPinchZoom(false)
        vChart.setDrawBarShadow(false)
        vChart.setDrawGridBackground(false)
        // create a custom MarkerView (extend MarkerView) and specify the layout
// to use for it
        val mv = MyMarkerView(requireContext(), R.layout.custom_marker_view)
        mv.chartView = vChart // For bounds control
        vChart.marker = mv // Set the marker to the chart

        vChart.legend?.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            orientation = Legend.LegendOrientation.VERTICAL
            setDrawInside(true)
            typeface = tfLight
            yOffset = 0f
            xOffset = 10f
            yEntrySpace = 0f
            textSize = 8f
        }

        vChart.xAxis.apply {
            typeface = tfLight
            granularity = 1f
            setCenterAxisLabels(true)
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toString()
                }
            }
        }

        vChart.axisLeft.apply {
            typeface = tfLight
            valueFormatter = LargeValueFormatter()
            setDrawGridLines(false)
            spaceTop = 35f
            axisMinimum = 0f // this replaces setStartAtZero(true)
        }
        vChart.axisRight?.isEnabled = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super<BonePersisterInterface>.onSaveInstanceState(outState)
        super<androidx.fragment.app.Fragment>.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<BonePersisterInterface>.onCreate(savedInstanceState)
        super<androidx.fragment.app.Fragment>.onCreate(savedInstanceState)
    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity)
//    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
//        val startYear = 1980
//        val endYear = startYear + groupCount

        setupText(1, vSeekBarX.max)
        if (seekBar == vSeekBarX && progress < 1) {
            viewModel.requestPortion(progress + 1, vSeekBarX.max)
        }
    }

    private fun applyData(data: BarData, barValues: ArrayList<BarEntry>, index: Int, portion: List<Pair<String, String>>) {
        val set = data.getDataSetByIndex(index) as BarDataSet
        set.apply {
            values = barValues
            label = portion[index].first
        }
    }

    @SuppressLint("NewApi")
    private fun showPortion(startYear: Int = 1, quantityPortions: Int, portion: List<Pair<String, String>>) {
        val groupSpace = 0.08f
        val barSpace = 0.03f // x4 DataSet
        val barWidth = 0.2f // x4 DataSet
        // (0.2 + 0.03) * 4 + 0.08 = 1.00 -> interval per "group"
        val groupCount = vSeekBarX.progress + 1

        val values1 = ArrayList<BarEntry>()
        val values2 = ArrayList<BarEntry>()
        val values3 = ArrayList<BarEntry>()
        val values4 = ArrayList<BarEntry>()

//        for (i in FIRST_INDEX until FOURTH_INDEX) {
            values1.add(BarEntry(FIRST_INDEX.toFloat(), portion[FIRST_INDEX].second.toFloat()))
            values2.add(BarEntry(SECOND_INDEX.toFloat(), portion[SECOND_INDEX].second.toFloat()))
            values3.add(BarEntry(THIRD_INDEX.toFloat(), portion[THIRD_INDEX].second.toFloat()))
            values4.add(BarEntry(FOURTH_INDEX.toFloat(), portion[FOURTH_INDEX].second.toFloat()))
//        }
        val set1: BarDataSet
        val set2: BarDataSet
        val set3: BarDataSet
        val set4: BarDataSet
        vChart.also { chart ->
            if (chart.data != null && chart.data.dataSetCount > 0) {
                applyData(chart.data, values1, FIRST_INDEX, portion)
                applyData(chart.data, values2, SECOND_INDEX, portion)
                applyData(chart.data, values3, THIRD_INDEX, portion)
                applyData(chart.data, values4, FOURTH_INDEX, portion)
                chart.data.notifyDataChanged()
                chart.notifyDataSetChanged()
            } else { // create 4 DataSets
                set1 = BarDataSet(values1, portion[FIRST_INDEX].first)
                set1.color = Color.rgb(104, 241, 175)
                set2 = BarDataSet(values2, portion[SECOND_INDEX].first)
                set2.color = Color.rgb(164, 228, 251)
                set3 = BarDataSet(values3, portion[THIRD_INDEX].first)
                set3.color = Color.rgb(242, 247, 158)
                set4 = BarDataSet(values4, portion[FOURTH_INDEX].first)
                set4.color = Color.rgb(255, 102, 0)
                val data = BarData(set1, set2, set3, set4)
                data.setValueFormatter(LargeValueFormatter())
                data.setValueTypeface(tfLight)
                chart.data = data
            }
            // specify the width each bar should have
            chart.barData.barWidth = barWidth
            // restrict the x-axis range
            chart.xAxis.axisMinimum = startYear.toFloat()
            // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
            chart.xAxis.axisMaximum = startYear + chart.barData.getGroupWidth(groupSpace, barSpace) * groupCount
            chart.groupBars(startYear.toFloat(), groupSpace, barSpace)
            chart.invalidate()
        }
        showNextPortion(quantityPortions)
    }

    private fun showNextPortion(quantityPortions: Int) {
        vSeekBarX.max = quantityPortions
//        vSeekBarX.progress = 1
        vSeekBarY.progress = 100
    }

    private fun setupText(startYear: Int, endYear: Int) {
        tvXMax.text = String.format(Locale.ENGLISH, "%d-%d", startYear, endYear)
        tvYMax.text = vSeekBarY!!.progress.toString()
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.actionToggleValues -> {
//                for (set in chart!!.data.dataSets) set.setDrawValues(!set.isDrawValuesEnabled)
//                chart!!.invalidate()
//            }
//            R.id.actionTogglePinch -> {
//                if (chart!!.isPinchZoomEnabled) chart!!.setPinchZoom(false) else chart!!.setPinchZoom(true)
//                chart!!.invalidate()
//            }
//            R.id.actionToggleAutoScaleMinMax -> {
//                chart!!.isAutoScaleMinMaxEnabled = !chart!!.isAutoScaleMinMaxEnabled
//                chart!!.notifyDataSetChanged()
//            }
//            R.id.actionToggleBarBorders -> {
//                for (set in chart!!.data.dataSets) (set as BarDataSet).barBorderWidth = if (set.getBarBorderWidth() == 1f) 0f else 1f
//                chart!!.invalidate()
//            }
//            R.id.actionToggleHighlight -> {
//                if (chart!!.data != null) {
//                    chart!!.data.isHighlightEnabled = !chart!!.data.isHighlightEnabled
//                    chart!!.invalidate()
//                }
//            }
//            R.id.animateX -> {
//                chart!!.animateX(2000)
//            }
//            R.id.animateY -> {
//                chart!!.animateY(2000)
//            }
//            R.id.animateXY -> {
//                chart!!.animateXY(2000, 2000)
//            }
//        }
//        return true
//    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}
    override fun onValueSelected(e: Entry, h: Highlight) {
        Log.i("Activity", "Selected: " + e.toString() + ", dataSet: " + h.dataSetIndex)
    }

    override fun onNothingSelected() {
        Log.i("Activity", "Nothing selected.")
    }

    companion object {
        private const val FIRST_INDEX = 0
        private const val SECOND_INDEX = 1
        private const val THIRD_INDEX = 2
        private const val FOURTH_INDEX = 3
    }
}