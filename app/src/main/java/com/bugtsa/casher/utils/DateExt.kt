package com.bugtsa.casher.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.core.content.ContextCompat
import com.bugtsa.casher.R
import com.bugtsa.casher.utils.DateConverter.setupNewTime
import com.bugtsa.casher.utils.DateConverter.toHoursValue
import com.bugtsa.casher.utils.DateConverter.toMinutesValue
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Context.showDateDialog(
    selectedDate: Calendar,
    positiveColorId: Int,
    negativeColorId: Int,
    dateCallback: ((Date) -> (Unit))
) {
    val dialog = DatePickerDialog(
        this,
        { _, year, monthOfYear, dayOfMonth ->
            selectedDate.set(year, monthOfYear, dayOfMonth)
            dateCallback.invoke(selectedDate.time)
        },
        selectedDate[Calendar.YEAR],
        selectedDate[Calendar.MONTH],
        selectedDate[Calendar.DAY_OF_MONTH]
    )
    dialog.setCancelable(false)
    dialog.setOnShowListener {
        val positiveColor = ContextCompat.getColor(this, positiveColorId)
        val negativeColor = ContextCompat.getColor(this, negativeColorId)
        dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(positiveColor)
        dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(negativeColor)
    }
    dialog.show()
}

fun Context.showDoubleTimeDialog(
    startDate: Date,
    endDate: Date,
    timeCallback: ((Pair<Date, Date>) -> (Unit)),
    positiveColorId: Int,
    negativeColorId: Int
) {
    showTimeDialog(
        startDate,
        R.string.order_info_time_start,
        positiveColorId,
        negativeColorId
    ) { start ->
        showTimeDialog(
            endDate,
            R.string.order_info_time_end,
            positiveColorId,
            negativeColorId
        ) { end ->
            timeCallback.invoke(start to end)
        }
    }
}

fun Context.showTimeDialog(
    date: Date,
    titleId: Int = DEFAULT_RES_ID,
    positiveColorId: Int,
    negativeColorId: Int,
    timeCallback: ((Date) -> (Unit))
) {
    val dialog = TimePickerDialog(
        this,
        { _, hour, minute ->
            timeCallback.invoke(date.setupNewTime(hour, minute))
        },
        date.time.toHoursValue(), date.time.toMinutesValue(), true
    )
    dialog.setCancelable(false)
    titleId
        .takeIf { it != DEFAULT_RES_ID }
        ?.also {
            dialog.setTitle(it)
        }

    dialog.setOnShowListener {
        val positiveColor = ContextCompat.getColor(this, positiveColorId)
        val negativeColor = ContextCompat.getColor(this, negativeColorId)
        dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(positiveColor)
        dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(negativeColor)
    }
    dialog.show()
}

fun getDefaultCalendar(): Calendar =
    Calendar.getInstance(Locale("ru", "RU"))

private const val DEFAULT_RES_ID = 0

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR