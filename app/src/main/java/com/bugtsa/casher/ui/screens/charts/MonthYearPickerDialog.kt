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
import java.util.*


class MonthYearPickerDialog : DialogFragment() {
    private var listener: DatePickerDialog.OnDateSetListener? = null
    fun setListener(listener: DatePickerDialog.OnDateSetListener?) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        val inflater: LayoutInflater = requireActivity().layoutInflater
        val cal: Calendar = Calendar.getInstance()
        val dialog: View = inflater.inflate(R.layout.dialog_month_picker, null)
        val monthPicker = dialog.findViewById(R.id.picker_month) as NumberPicker
        val yearPicker = dialog.findViewById(R.id.picker_year) as NumberPicker
        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = cal.get(Calendar.MONTH)
        val year: Int = cal.get(Calendar.YEAR)
        yearPicker.minValue = MIN_YEAR
        yearPicker.maxValue = year
        yearPicker.value = year
        builder.setView(dialog) // Add action buttons
                .setPositiveButton(R.string.ok_caption) { _, _ -> listener?.onDateSet(null, yearPicker.value, monthPicker.value - 1, 0) }
                .setNegativeButton(R.string.cancel_caption) { _, _ -> this@MonthYearPickerDialog.dialog?.cancel() }
        return builder.create()
    }

    companion object {
        private const val MIN_YEAR = 2018
    }
}