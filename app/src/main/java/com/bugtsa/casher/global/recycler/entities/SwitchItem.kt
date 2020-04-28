package com.bugtsa.casher.global.recycler.entities

import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

abstract class BaseSwitchItem(
        val textLabel: String? = null,
        val onCheckedChangeListener: CompoundButton.OnCheckedChangeListener,
        val initiallyVisible: Boolean = true,
        val state: LiveData<Boolean>? = null,
        val visible: LiveData<Boolean>? = null
) : ListItem() {
    val enable: MutableLiveData<Boolean> = MutableLiveData()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseSwitchItem

        if (textLabel != other.textLabel) return false
        if (state != other.state) return false
        if (visible != other.visible) return false
        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (textLabel?.hashCode() ?: 0)
        result = 31 * result + (state?.hashCode() ?: 0)
        result = 31 * result + (visible?.hashCode() ?: 0)
        return result
    }

    fun setEnable(isEnables: Boolean) {
        enable.value = isEnables
    }
}

class SwitchItem(
        textLabel: String?,
        onCheckedChangeListener: CompoundButton.OnCheckedChangeListener,
        state: LiveData<Boolean>? = null,
        initiallyVisible: Boolean = true,
        visible: LiveData<Boolean>? = null
) : BaseSwitchItem(textLabel, onCheckedChangeListener, state = state, initiallyVisible = initiallyVisible, visible = visible)

class ProfileSwitchItem(
        textLabel: String?,
        onCheckedChangeListener: CompoundButton.OnCheckedChangeListener,
        state: LiveData<Boolean>? = null
) : BaseSwitchItem(textLabel, onCheckedChangeListener, state = state)
