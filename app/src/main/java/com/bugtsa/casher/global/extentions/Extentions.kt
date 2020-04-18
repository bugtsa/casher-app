package com.bugtsa.casher.global.extentions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import java.util.*

fun Int.getMonthName(locale: Locale, shortName: Boolean): String {
    var format = "%tB"
    if (shortName) format = "%tb"
    val calendar = Calendar.getInstance(locale)
    calendar[Calendar.MONTH] = this
    calendar[Calendar.DAY_OF_MONTH] = 1
    return java.lang.String.format(locale, format, calendar)
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View =
        LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

fun AlertDialog.Builder.positiveButton(text: String = "OK", handleClick: (which: Int) -> Unit = {}) {
    this.setPositiveButton(text) { _, which -> handleClick(which) }
}