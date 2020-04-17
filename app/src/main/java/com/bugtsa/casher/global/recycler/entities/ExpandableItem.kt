package com.bugtsa.casher.global.recycler.entities

import com.bugtsa.casher.R
import com.bugtsa.casher.global.recycler.entities.internal.ExpandableHideButtonItem


data class ExpandableItem(
        private val elements: List<ListItem>,
        val expandDefault: Boolean = false,
        val expandText: Int = R.string.profile_expandable_view_text,
        val clickListener: (() -> Unit)? = null
) : ListItem() {

    internal var expandFlag: Boolean? = null
    internal var hideListener: (() -> Unit)? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as ExpandableItem

        if (elements != other.elements) return false
        if (expandDefault != other.expandDefault) return false
        if (expandText != other.expandText) return false
        if (clickListener != other.clickListener) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + elements.hashCode()
        result = 31 * result + expandDefault.hashCode()
        result = 31 * result + expandText.hashCode()
        result = 31 * result + clickListener.hashCode()
        return result
    }

    fun expandElements() = elements + listOf(ExpandableHideButtonItem(elements.size))
}
