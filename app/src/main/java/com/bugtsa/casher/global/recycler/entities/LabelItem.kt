package com.bugtsa.casher.global.recycler.entities

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.bugtsa.casher.R

data class LabelItem(
        val textLabel: String? = null,
        val isAllCaps: Boolean = true,
        @ColorRes val textColor: Int = R.color.secondaryDarkColor,
        @ColorRes val backgroundColor: Int = R.color.textLightColor,
        val textSize: Int = 16,
        @DrawableRes val background: Int? = null,
        val topBottomMargins: Int = 8
): ListItem()