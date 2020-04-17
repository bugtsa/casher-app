package com.bugtsa.casher.global.extentions

import android.content.Context
import android.util.DisplayMetrics

fun Context.convertDpToPx(dp: Int): Int {
    return Math.round(dp * (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}