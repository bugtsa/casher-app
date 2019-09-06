package com.bugtsa.casher.utils

import android.view.View

fun View.visibility(predicate: () -> Boolean?) {
    this.visibility = if (predicate() == true) View.VISIBLE else View.GONE
}