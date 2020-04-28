package com.bugtsa.casher.global.extentions

import android.content.Context
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment

@RequiresApi(Build.VERSION_CODES.CUPCAKE)
fun Fragment.hideKeyboard() {
    (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.let { inputMethodManager ->
        activity?.currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.CUPCAKE)
fun Fragment.toggleKeyboard() {
    (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun Fragment.showKeyboard(view: View) {
    (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.showSoftInput(view, InputMethodManager.SHOW_FORCED)
}