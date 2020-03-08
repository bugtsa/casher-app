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
import com.bugtsa.casher.presentation.chart.ChangedDateRange
import com.bugtsa.casher.presentation.chart.ChooseChartsViewModel
import com.bugtsa.casher.presentation.chart.ChooseChartsViewModel.Companion.SORT_ASC
import com.bugtsa.casher.presentation.chart.ChooseChartsViewModel.Companion.SORT_DESC
import com.bugtsa.casher.presentation.chart.ChooseChartsViewModel.Companion.SORT_UNSORTED
import com.bugtsa.casher.presentation.chart.ChooseChartsViewModel.Companion.monthCalendarValue
import com.bugtsa.casher.presentation.chart.ChooseChartsViewModelFactory
import com.bugtsa.casher.presentation.chart.DateRange
import com.bugtsa.casher.ui.screens.settings.NavigationStackPresentable
import com.bugtsa.casher.utils.getMonthName
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

    private lateinit var viewModelChoose: ChooseChartsViewModel

    private val startDateChangeListener = object : StartDateRangeChangeListener {
        override fun startOnChanged(dateRange: DateRange) {
            viewModelChoose.setupStartRange(ChangedDateRange(dateRange))
        }
    }

    private val endDateChangeListener = object : EndDateRangeChangeListener {
        override fun endOnChanged(dateRange: DateRange) {
            viewModelChoose.setupEndRange(ChangedDateRange(dateRange))
        }
    }

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
        vChooseStartMonth.setOnClickListener(showMonthPicker(startDateDialog, vStartDate, startDateChangeListener))
        vChooseEndMonth.setOnClickListener(showMonthPicker(endDateDialog, vEndDate, endDateChangeListener))
        vShowChart.setOnClickListener { bone.show(BarChartScreen(viewModelChoose.getPreference())) }
        vShowPieChart.setOnClickListener { bone.show(PieChartScreen(viewModelChoose.getPreference())) }
    }

    private fun bindView() {
        vShowChart.visibility = View.GONE

        vSortTypeRadio.setOnCheckedChangeListener { _, checkedId ->
            val sortType = when (checkedId) {
                vTypeSortAsc.id -> SORT_ASC
                vTypeSortDesc.id -> SORT_DESC
                vTypeUnSort.id -> SORT_UNSORTED
                else -> SORT_UNSORTED
            }
            viewModelChoose.setupTypeSort(sortType)
        }
    }

    private fun bindViewModel() {
        viewModelChoose.observeDialogsRangeMonth().observe(viewLifecycleOwner, androidx.lifecycle.Observer { (startDateRange, endDateRange) ->
            startDateDialog.setRangeDate(startDateRange, endDateRange)
            endDateDialog.setRangeDate(startDateRange, endDateRange)
            vShowChart.visibility = View.VISIBLE
            vStartDate.text = getString(R.string.month_range_start,
                    startDateRange.year.toString(),
                    startDateRange.month.getMonthName(Locale.getDefault(), false))
            vEndDate.text = getString(R.string.month_range_end,
                    endDateRange.year.toString(),
                    endDateRange.month.getMonthName(Locale.getDefault(), false))
        })
    }

    private fun bindDataSetListener(textView: TextView, changedDateRange: DateRangeChangeListener): DatePickerDialog.OnDateSetListener {
        return DatePickerDialog.OnDateSetListener { _, year, month, _ ->
            val stringMonth = monthCalendarValue(month).getMonthName(Locale.getDefault(), false)
            textView.text = getString(R.string.month_range_start, year.toString(), stringMonth)
            if (changedDateRange is StartDateRangeChangeListener) {
                changedDateRange.startOnChanged(DateRange(month, year))
            } else if (changedDateRange is EndDateRangeChangeListener) {
                changedDateRange.endOnChanged(DateRange(month, year))
            }
        }
    }

    private fun showMonthPicker(dateDialog: MonthYearPickerDialog, textView: TextView, changedDateRange: DateRangeChangeListener): View.OnClickListener? {
        return View.OnClickListener {
            val dateSetListener = bindDataSetListener(textView, changedDateRange)
            dateDialog.setListener(dateSetListener)
            dateDialog.show(requireActivity().supportFragmentManager, "MonthYearPickerDialog")
        }
    }
}

interface StartDateRangeChangeListener : DateRangeChangeListener {
    fun startOnChanged(dateRange: DateRange)
}

interface EndDateRangeChangeListener : DateRangeChangeListener {
    fun endOnChanged(dateRange: DateRange)
}

interface DateRangeChangeListener