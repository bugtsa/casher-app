package com.bugtsa.casher.global.extentions

import android.view.View

fun <T : View> T.enabled(expression: () -> Boolean?): T {
    val exp = expression()
    this.isEnabled = exp == true
    this.alpha = if (exp == true) 1.0f else 0.5f

    return this
}

fun <T : View> T.visibility(expression: () -> Boolean?): T {
    this.visibility = if (expression() == true) View.VISIBLE else View.GONE

    return this
}