package com.bugtsa.casher.global.recycler.entities

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.bugtsa.casher.R

data class DataItem(
        val textLabel: String? = null,
        val textData: String? = null,
        val isAllCaps: Boolean = true,
        @DrawableRes val backgroundRes: Int = R.color.primaryColor,
        @ColorRes val labelTextColor: Int = R.color.primaryDarkColor,
        @ColorRes val dataTextColor: Int = R.color.secondaryTextColor,
        @DrawableRes val iconRes: Int? = null,
        val action: (() -> Unit)? = null
) : ListItem() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataItem

        if (textLabel != other.textLabel) return false
        if (textData != other.textData) return false
        if (isAllCaps != other.isAllCaps) return false
        if (backgroundRes != other.backgroundRes) return false
        if (labelTextColor != other.labelTextColor) return false
        if (iconRes != other.iconRes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (textLabel?.hashCode() ?: 0)
        result = 31 * result + (textData?.hashCode() ?: 0)
        result = 31 * result + isAllCaps.hashCode()
        result = 31 * result + backgroundRes
        result = 31 * result + labelTextColor
        result = 31 * result + (iconRes ?: 0)
        return result
    }
}
