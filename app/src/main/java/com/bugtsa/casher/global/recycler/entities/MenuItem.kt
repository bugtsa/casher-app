package com.bugtsa.casher.global.recycler.entities

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.lifecycle.LiveData
import com.bugtsa.casher.R

class MenuItem(
        val title: String,
        val subTitleLiveDataResId: LiveData<Int>? = null,
        val isArrowEnabled: Boolean = true,
        val counter: Int = 0,
        @ColorRes val textColor: Int = R.color.primaryTextColor,
        @DrawableRes val icon: Int? = null,
        @DrawableRes val counterBackground: Int = R.drawable.red_circle,
        @ColorRes val counterTextColor: Int = R.color.primaryColor,
        val initiallyVisible: Boolean = true,
        val visible: LiveData<Boolean>? = null
): ListItem() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as MenuItem

        if (title != other.title) return false
        if (isArrowEnabled != other.isArrowEnabled) return false
        if (counter != other.counter) return false
        if (textColor != other.textColor) return false
        if (icon != other.icon) return false
        if (counterBackground != other.counterBackground) return false
        if (counterTextColor != other.counterTextColor) return false
        if (visible != other.visible) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + isArrowEnabled.hashCode()
        result = 31 * result + counter
        result = 31 * result + textColor
        result = 31 * result + (icon ?: 0)
        result = 31 * result + counterBackground
        result = 31 * result + counterTextColor
        result = 31 * result + (visible?.hashCode() ?: 0)
        return result
    }
}