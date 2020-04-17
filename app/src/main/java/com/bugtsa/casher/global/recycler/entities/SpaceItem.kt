package com.bugtsa.casher.global.recycler.entities

import androidx.annotation.ColorRes
import com.bugtsa.casher.R

class SpaceItem(
        val height: Int,
        @ColorRes val backgroundColor: Int = R.color.textLightColor
): ListItem() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as SpaceItem

        if (height != other.height) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + height
        return result
    }
}