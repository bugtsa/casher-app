package com.bugtsa.casher.global.recycler.entities

import androidx.annotation.ColorRes
import androidx.lifecycle.LiveData
import com.bugtsa.casher.R

data class DividerItem(
        @ColorRes val backgroundColor: Int = R.color.colorPrimaryDark,
        val leftMargin: Int = 24,
        val rightMargin: Int = 24,
        val backgroundOther: Int = R.color.primaryColor,
        val initiallyVisible: Boolean = true,
        val visible: LiveData<Boolean>? = null
) : ListItem() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as DividerItem

        if (backgroundColor != other.backgroundColor) return false
        if (visible != other.visible) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + backgroundColor
        result = 31 * result + (visible?.hashCode() ?: 0)
        return result
    }
}