package com.bugtsa.casher.global.extentions

import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.text.SpannableStringBuilder
import android.util.DisplayMetrics
import android.util.TypedValue
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.view.setPadding
import com.bugtsa.casher.R
import com.bugtsa.casher.utils.ConstantManager.Constants.EMPTY

fun Context.convertDpToPx(dp: Int): Int {
    return Math.round(dp * (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}

fun Number.dp() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics)

fun Context.showAlertDialog(title: String,
                            customTitle: String = EMPTY,
                            message: String,
                            positiveListener: DialogInterface.OnClickListener? = null,
                            positiveText: String = this.getString(android.R.string.ok),
                            negativeListener: DialogInterface.OnClickListener? = null,
                            negativeText: String = this.getString(android.R.string.no),
                            cancelListener: DialogInterface.OnCancelListener? = null,
                            colorCustomTitle: Int? = null,
                            colorNegativeText: Int? = null): AlertDialog {
    val builder = AlertDialog
            .Builder(this, R.style.DialogInfo)
            .apply {
                setNegativeButton(negativeText) { dialog, which ->
                    negativeListener?.onClick(dialog, which)
                }
                setPositiveButton(positiveText) { dialog, which ->
                    positiveListener?.onClick(dialog, which)
                }
                setOnCancelListener { dialog ->
                    cancelListener?.onCancel(dialog)
                }
                if (customTitle.isNotEmpty()) {
                    TextView(context)
                            .apply {
                                setPadding(24.dp().toInt())
                                setTextColor(ContextCompat.getColor(context, colorCustomTitle
                                        ?: R.color.primaryTextColor))
                                setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
                                text = SpannableStringBuilder()
                                        .bold {
                                            append(customTitle)
                                        }
                            }
                            .also { setCustomTitle(it) }
                } else {
                    setTitle(title)
                }
                setMessage(message)
            }
    val dialog = builder.create()
    colorNegativeText?.also { color ->
        dialog.setOnShowListener {
            val negativeButton: Button = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            negativeButton.setTextColor(ContextCompat.getColor(this, color))
            negativeButton.invalidate()
        }
    }
    return dialog
}