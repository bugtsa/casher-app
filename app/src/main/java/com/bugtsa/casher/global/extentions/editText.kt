package com.bugtsa.casher.global.extentions

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.bugtsa.casher.R
import com.bugtsa.casher.utils.ConstantManager

private const val DRAWABLE_RIGHT_ATTR = 2

fun getDrawable(@ColorRes colorAccentResId: Int,
                @DrawableRes drawableResId: Int,
                context: Context): Drawable? {
    val drawable = AppCompatResources.getDrawable(context, drawableResId)

    return drawable?.let {
        val wrappedDrawable = DrawableCompat.wrap(it)
        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(context, colorAccentResId))
        wrappedDrawable
    }
}

fun EditText.setupPassVisibility() {
    transformationMethod = PasswordTransformationMethod.getInstance()
    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
}

fun EditText.showStatePassVisibility(
        colorAccentResId: Int,
        isVisiblePassword: Boolean,
        context: Context) {
    var visibilityDrawable = if (isVisiblePassword) {
        AppCompatResources.getDrawable(context, R.drawable.ic_visibility_off_24dp)
    } else {
        AppCompatResources.getDrawable(context, R.drawable.ic_visibility_24dp)
    }
    visibilityDrawable?.also { drawable ->
        visibilityDrawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(drawable, ContextCompat.getColor(context, colorAccentResId))
        setCompoundDrawablesWithIntrinsicBounds(null, null, visibilityDrawable, null)
    }
}

fun EditText.togglePassVisibility(isPasswordVisible: Boolean): Boolean {
    if (isPasswordVisible) {
        val pass: String = text.toString()
        transformationMethod = PasswordTransformationMethod.getInstance()
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        setText(pass)
        setSelection(pass.length)
    } else {
        val pass: String = text.toString()
        transformationMethod = HideReturnsTransformationMethod.getInstance()
        inputType = InputType.TYPE_CLASS_TEXT
        setText(pass)
        setSelection(pass.length)
    }
    return !isPasswordVisible
}

fun EditText.getOnTouchIncludeRightDrawableClick(
        commonAction: (() -> Unit),
        rightDrawableAction: (() -> Unit)
): View.OnTouchListener {
    return View.OnTouchListener { _, event ->
        when {
            event.action == MotionEvent.ACTION_UP &&
                    this.compoundDrawables[DRAWABLE_RIGHT_ATTR] != null
                    &&  event.rawX >= this.right - this.compoundDrawables[DRAWABLE_RIGHT_ATTR].bounds.width() -> {
                rightDrawableAction.invoke()
                return@OnTouchListener true
            }
            event.action == MotionEvent.ACTION_UP -> {
                commonAction.invoke()
                return@OnTouchListener true
            }
        }
        false
    }
}

fun EditText.getOnTouchRightDrawableClickListener(action: (() -> Unit)): View.OnTouchListener {
    return View.OnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_UP && this.compoundDrawables[DRAWABLE_RIGHT_ATTR] != null) {
            if (event.rawX >= this.right - this.compoundDrawables[DRAWABLE_RIGHT_ATTR].bounds.width()) {
                action.invoke()
                return@OnTouchListener true
            }
        }
        false
    }
}

fun EditText.resetInput() {
    this.requestFocus()
    this.isCursorVisible = true
    clearEditText()
}

fun EditText.clearEditText() {
    setText(ConstantManager.Constants.EMPTY)
    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
}