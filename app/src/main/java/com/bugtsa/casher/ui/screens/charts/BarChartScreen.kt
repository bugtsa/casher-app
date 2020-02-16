package com.bugtsa.casher.ui.screens.charts

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bugtsa.casher.R
import com.bugtsa.casher.presentation.chart.BarChartViewModel
import com.bugtsa.casher.presentation.chart.BarChartViewModelFactory
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.android.synthetic.main.fragment_barchart.*
import pro.horovodovodo4ka.bones.Phalanx
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.ui.FragmentSibling
import pro.horovodovodo4ka.bones.ui.delegates.Page
import toothpick.Toothpick

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

    private lateinit var viewModel: BarChartViewModel
    private val commonTextSize = 10f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_barchart, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModelFactory = Toothpick
                .openScopes(requireActivity(), this)
                .getInstance(BarChartViewModelFactory::class.java)
        viewModel = ViewModelProvider(this, viewModelFactory)[BarChartViewModel::class.java]

        setupSeekBars()
        setupChart()

        bindViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super<BonePersisterInterface>.onSaveInstanceState(outState)
        super<androidx.fragment.app.Fragment>.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<BonePersisterInterface>.onCreate(savedInstanceState)
        super<androidx.fragment.app.Fragment>.onCreate(savedInstanceState)
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (seekBar == vSeekBarY) {
            viewModel.requestPortion(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}
    override fun onValueSelected(e: Entry, h: Highlight) {}
    override fun onNothingSelected() {}

    private fun bindViewModel() {
        viewModel.observeChartData().observe(viewLifecycleOwner, androidx.lifecycle.Observer { chartData ->
            showPortion(chartData.portion)
        })

        viewModel.observeQuantityPortions().observe(viewLifecycleOwner, androidx.lifecycle.Observer { quantity ->
            setupAxisTitle(quantity)
            vSeekBarY.max = quantity
            vSeekBarY.progress = 0
        })
    }

    private fun setupSeekBars() {
        vSeekBarX.setOnSeekBarChangeListener(this)
        vSeekBarY.setOnSeekBarChangeListener(this)
    }

    private fun setupChart() {
        tvXMax.textSize = commonTextSize
        vChart.setOnChartValueSelectedListener(this)
        vChart.description?.isEnabled = false
        vChart.setPinchZoom(false)
        vChart.setDrawBarShadow(false)
        vChart.setDrawGridBackground(false)
        // create a custom MarkerView (extend MarkerView) and specify the layout
// to use for it
        val mv = MyMarkerView(requireContext(), R.layout.view_marker_bar_chart)
        ContextCompat.getDrawable(requireContext(), R.drawable.background_marker_bar_chart)?.also { drawable ->
            drawable.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(R.color.secondaryColor, BlendModeCompat.SRC_IN)
        }
        mv.chartView = vChart // For bounds control
        vChart.marker = mv // Set the marker to the chart

        vChart.legend?.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            orientation = Legend.LegendOrientation.VERTICAL
            setDrawInside(true)
            yOffset = 0f
            xOffset = 10f
            yEntrySpace = 0f
            textSize = commonTextSize
        }

        vChart.xAxis.apply {
            granularity = 1f
            setCenterAxisLabels(true)
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toString()
                }
            }
        }

        vChart.axisLeft.apply {
            valueFormatter = LargeValueFormatter()
            setDrawGridLines(false)
            spaceTop = 70f
            axisMinimum = 0f
            textSize = commonTextSize
        }
        vChart.axisRight?.isEnabled = false

        bone.chartPreference?.also { preference ->
            viewModel.requestChartData(preference)
        }
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity)
//    }

    private var maxCost = 0f

    @SuppressLint("NewApi")
    private fun showPortion(portion: List<Pair<String, String>>) {
        val groupSpace = 0.08f
        val barSpace = 0.03f // x4 DataSet
        val barWidth = 0.2f // x4 DataSet
        // (0.2 + 0.03) * 4 + 0.08 = 1.00 -> interval per "group"
        val groupCount = vSeekBarX.progress + 1

        val listValues = mutableListOf<Triple<Int, String, ArrayList<BarEntry>>>()
        portion.forEach { (title, cost) ->
            cost.toFloat().also {valueCost ->
                if (maxCost < valueCost) {
                    maxCost = valueCost
                }
            }
            listValues.add(listValues.size.let { index ->
                arrayListOf(BarEntry(index.toFloat(), cost.toFloat()))
                        .let { values ->
                            Triple(index, title, values)
                        }
            })
        }
        vChart.also { chart ->
            if (chart.data != null && chart.data.dataSetCount > 0) {
                listValues.forEach { (index, title, barValues) ->
                    (chart.data.getDataSetByIndex(index) as BarDataSet).apply {
                        values = barValues
                        label = title

                    }
                }
                chart.data.notifyDataChanged()
                chart.notifyDataSetChanged()
            } else {
                val listSets = BarData()
                listValues.forEach { (index, title, values) ->
                    val basDataSet = BarDataSet(values, title)
                    basDataSet.color = getColor(index)
                    listSets.addDataSet(basDataSet)
                }
                listSets.setValueFormatter(LargeValueFormatter())
                chart.data = listSets
            }
            chart.apply {
                barData.barWidth = barWidth
                // restrict the x-axis range
                xAxis.axisMinimum = START_INDEX.toFloat()
                // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
                xAxis.axisMaximum = START_INDEX + chart.barData.getGroupWidth(groupSpace, barSpace) * groupCount
                axisLeft.mAxisMaximum = maxCost + maxCost * 0.1f
                groupBars(START_INDEX.toFloat(), groupSpace, barSpace)
                invalidate()
            }
        }
    }

    private fun getColor(index: Int): Int {
        return when (index) {
            FIRST_INDEX -> Color.rgb(104, 241, 175)
            SECOND_INDEX -> Color.rgb(164, 228, 251)
            THIRD_INDEX -> Color.rgb(242, 247, 158)
            FOURTH_INDEX -> Color.rgb(255, 102, 0)
            else -> Color.rgb(189, 180, 180)
        }
    }

    private fun setupAxisTitle(quantityPortions: Int) {
        tvXMax.text = getString(R.string.charts_caption_x_axis)
        tvYMax.text = getString(R.string.charts_caption_y_axis, START_INDEX, quantityPortions)
    }

    companion object {
        private const val FIRST_INDEX = 0
        private const val SECOND_INDEX = 1
        private const val THIRD_INDEX = 2
        private const val FOURTH_INDEX = 3

        private const val START_INDEX = 1
    }
}