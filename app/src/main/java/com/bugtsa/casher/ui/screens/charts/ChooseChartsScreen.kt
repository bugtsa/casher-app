package com.bugtsa.casher.ui.screens.charts

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.bugtsa.casher.R
import com.bugtsa.casher.presentation.chart.ChooseChartsViewModel
import com.bugtsa.casher.presentation.chart.ChooseChartsViewModel.Companion.SORT_MODE_DEFAULT
import com.bugtsa.casher.presentation.chart.ChooseChartsViewModelFactory
import com.bugtsa.casher.ui.screens.settings.NavigationStackPresentable
import kotlinx.android.synthetic.main.fragment_choose_charts.*
import pro.horovodovodo4ka.bones.Phalanx
import pro.horovodovodo4ka.bones.extensions.show
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.ui.FragmentSibling
import pro.horovodovodo4ka.bones.ui.delegates.Page
import toothpick.Toothpick
import java.util.*


class ChooseChartsScreen : Phalanx(), NavigationStackPresentable {

    override val seed = { ChartsScreenFragment() }

    override val fragmentTitle: String
        get() = "Charts"
}

@SuppressLint("MissingSuperCall")
class ChartsScreenFragment : androidx.fragment.app.Fragment(),
        BonePersisterInterface<ChooseChartsScreen>,
        FragmentSibling<ChooseChartsScreen> by Page() {

    private val startDateDialog = MonthYearPickerDialog()
    private val endDateDialog = MonthYearPickerDialog()
    private lateinit var chartPreference: ChartPreference

    private lateinit var viewModelChoose: ChooseChartsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_choose_charts, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chartsViewModelFactory = Toothpick
                .openScopes(requireActivity(), this)
                .getInstance(ChooseChartsViewModelFactory::class.java)
        viewModelChoose = ViewModelProvider(this, chartsViewModelFactory)[ChooseChartsViewModel::class.java]
        bindView()
        bindListeners()
        bindViewModel()
        refreshUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Toothpick.closeScope(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super<BonePersisterInterface>.onSaveInstanceState(outState)
        super<androidx.fragment.app.Fragment>.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<BonePersisterInterface>.onCreate(savedInstanceState)
        super<androidx.fragment.app.Fragment>.onCreate(savedInstanceState)
    }

    private fun bindListeners() {
        vChooseStartMonth.setOnClickListener(showMonthPicker(startDateDialog, vStartDate))
        vChooseEndMonth.setOnClickListener(showMonthPicker(endDateDialog, vEndDate))
        vShowChart.setOnClickListener { bone.show(BarChartScreen(chartPreference)) }
    }

    private fun bindView() {
        vShowChart.visibility = View.GONE
    }

    private fun bindViewModel() {
        viewModelChoose.observeRangeMonth().observe(viewLifecycleOwner, androidx.lifecycle.Observer { (startDateRange, endDateRange) ->
            startDateDialog.setRangeDate(startDateRange, endDateRange)
            endDateDialog.setRangeDate(startDateRange, endDateRange)
            vShowChart.visibility = View.VISIBLE
            chartPreference = ChartPreference(startDateRange, endDateRange, SORT_MODE_DEFAULT)
            vStartDate.text = getString(R.string.month_range_start,
                    startDateRange.year.toString(),
                    getMonthName(startDateRange.month, Locale.getDefault(), false))
            vEndDate.text = getString(R.string.month_range_end,
                    endDateRange.year.toString(),
                    getMonthName(endDateRange.month, Locale.getDefault(), false))
        })
    }

    private fun showMonthPicker(dateDialog: MonthYearPickerDialog, textView: TextView): View.OnClickListener? {
        return View.OnClickListener {
            val listener = DatePickerDialog.OnDateSetListener{
                _, year, month, _ ->
                val stringMonth = getMonthName(month, Locale.getDefault(), false)
                textView.text = getString(R.string.month_range_start, year.toString(), stringMonth)
            }
            dateDialog.setListener(listener)
            dateDialog.show(requireActivity().supportFragmentManager, "MonthYearPickerDialog")
        }
    }

    private fun getMonthName(index: Int, locale: Locale, shortName: Boolean): String? {
        var format = "%tB"
        if (shortName) format = "%tb"
        val calendar = Calendar.getInstance(locale)
        calendar[Calendar.MONTH] = index
        calendar[Calendar.DAY_OF_MONTH] = 1
        return java.lang.String.format(locale, format, calendar)
    }
}