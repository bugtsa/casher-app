package com.bugtsa.casher.global.recycler.delegates

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bugtsa.casher.R
import com.bugtsa.casher.global.extentions.convertDpToPx
import com.bugtsa.casher.global.extentions.inflate
import com.bugtsa.casher.global.recycler.delegates.holders.BaseViewHolder
import com.bugtsa.casher.global.recycler.entities.DividerItem
import com.bugtsa.casher.global.recycler.entities.ListItem
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate

class DividerDelegateAdapter : AdapterDelegate<List<ListItem>>() {

    override fun onCreateViewHolder(parent: ViewGroup) = DividerViewHolder(parent.inflate(R.layout.view_adapter_divider_item))

    override fun isForViewType(items: List<ListItem>, position: Int): Boolean = items[position] is DividerItem

    override fun onBindViewHolder(items: List<ListItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        val item = items[position] as DividerItem
        with(holder as DividerViewHolder) {
            bind(item)
        }
    }

    class DividerViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val divider: View = itemView.findViewById(R.id.vDivider)
        private val root: View = itemView.findViewById(R.id.vRoot)

        private var visibleLiveData: LiveData<Boolean>? = null
        private lateinit var visibleObserver: Observer<Boolean>

        override fun unbind() {
            visibleLiveData?.removeObserver(visibleObserver)
            super.unbind()
        }

        fun bind(dividerItem: DividerItem) {
            val leftMargin = itemView.context.convertDpToPx(dividerItem.leftMargin)
            val rightMargin = itemView.context.convertDpToPx(dividerItem.rightMargin)

            val layoutParams = divider.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(leftMargin, 0, rightMargin, 0)

            divider.setBackgroundColor(ContextCompat.getColor(divider.context, dividerItem.backgroundColor))

            root.setBackgroundColor(ContextCompat.getColor(divider.context, dividerItem.backgroundOther))

            root.isVisible = dividerItem.initiallyVisible
            visibleLiveData = dividerItem.visible
            visibleObserver = Observer {
                root.isVisible = it
            }

            visibleLiveData?.observe((root.context as LifecycleOwner), visibleObserver)

        }
    }
}