package com.bugtsa.casher.ui.screens.charts

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.bugtsa.casher.R
import com.bugtsa.casher.presentation.chart.DialogDateRange
import java.text.DateFormatSymbols
import java.util.*


class MonthYearPickerDialog : DialogFragment() {
    private val symbols = DateFormatSymbols()
    private val cal: Calendar = Calendar.getInstance()
    private val currentYear: Int = cal.get(Calendar.YEAR)

    private var listener: DatePickerDialog.OnDateSetListener? = null
    private var minMonth = MIN_MONTH
    private var minYear = MIN_YEAR
    private var maxYear = currentYear

    fun setListener(listener: DatePickerDialog.OnDateSetListener?) {
        this.listener = listener
    }

    fun setRangeDate(minDate: DialogDateRange, maxDate: DialogDateRange) {
        minMonth = minDate.month
        minYear = minDate.year
        maxYear = maxDate.year
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        val inflater: LayoutInflater = requireActivity().layoutInflater
        val dialog: View = inflater.inflate(R.layout.dialog_month_picker, null)
        val monthPicker = dialog.findViewById(R.id.picker_month) as NumberPicker
        val yearPicker = dialog.findViewById(R.id.picker_year) as NumberPicker

        monthPicker.minValue = minMonth
        monthPicker.maxValue = MAX_MONTH
        monthPicker.value = monthValueFromCalendar(cal.get(Calendar.MONTH))
        monthPicker.displayedValues = symbols.months

        yearPicker.minValue = minYear
        yearPicker.maxValue = maxYear
        yearPicker.value = currentYear
        builder.setView(dialog) // Add action buttons
                .setPositiveButton(android.R.string.ok) { _, _ -> listener?.onDateSet(null, yearPicker.value, monthPicker.value, 0) }
                .setNegativeButton(R.string.cancel_caption) { _, _ -> this@MonthYearPickerDialog.dialog?.cancel() }
        return builder.create()
    }

    companion object {
        const val MIN_MONTH = 1
        private const val MAX_MONTH = 12
        const val MIN_YEAR = 2019

        fun monthValueFromCalendar(calenderMonth: Int): Int = calenderMonth + 1
    }
}