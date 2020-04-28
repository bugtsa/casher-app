package com.bugtsa.casher.global.recycler.delegates.holders

import android.view.View
import android.widget.Switch
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import androidx.lifecycle.*
import com.bugtsa.casher.R
import com.bugtsa.casher.global.recycler.entities.BaseSwitchItem
import com.bugtsa.casher.global.recycler.entities.ListItem

abstract class BaseSwitchViewHolder(view: View) : BaseViewHolder(view), LifecycleOwner {

    protected val switch: Switch = itemView.findViewById(R.id.vSwitchHolder)

    private lateinit var stateObserver: Observer<Boolean>
    private var stateLiveData: LiveData<Boolean>? = null

    private var visibleLiveData: LiveData<Boolean>? = null
    private lateinit var visibleObserver: Observer<Boolean>

    private var enableLiveData: LiveData<Boolean>? = null
    private lateinit var enableObserver: Observer<Boolean>


    private val bindLifecycle = LifecycleRegistry(this).apply { currentState = Lifecycle.State.INITIALIZED }

    override fun getLifecycle(): Lifecycle {
        return bindLifecycle
    }

    @CallSuper
    override fun bind(item: ListItem) {

        bindLifecycle.currentState = Lifecycle.State.STARTED

        item as BaseSwitchItem
        updateText(item.textLabel)
        switch.setOnCheckedChangeListener(item.onCheckedChangeListener)
        updateSwitchText(item.state?.value ?: false)

        itemView.isVisible = item.initiallyVisible
        visibleLiveData = item.visible
        visibleObserver = Observer {
            itemView.isVisible = it
        }

        visibleLiveData?.observe(this, visibleObserver)

        enableLiveData = item.enable
        enableObserver = Observer {
            switch.isEnabled = it
        }

        enableLiveData?.observe(this, enableObserver)

        stateLiveData = item.state
        stateObserver = Observer { isChecked ->
            if (switch.isChecked != isChecked) {
                switch.setOnCheckedChangeListener(null)
                switch.isChecked = isChecked
                switch.setOnCheckedChangeListener(item.onCheckedChangeListener)
            }
            updateSwitchText(isChecked)
        }

        stateLiveData?.observe((switch.context as LifecycleOwner), stateObserver)
    }

    protected open fun updateSwitchText(isChecked: Boolean) = Unit

    override fun unbind() {
        bindLifecycle.currentState = Lifecycle.State.DESTROYED
        stateLiveData?.removeObserver(stateObserver)
        visibleLiveData?.removeObserver(visibleObserver)
        enableLiveData?.removeObserver(enableObserver)
        super.unbind()
    }

    protected abstract fun updateText(text: String?)

}