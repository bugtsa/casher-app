package com.bugtsa.casher.global.recycler.delegates

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bugtsa.casher.R
import com.bugtsa.casher.global.extentions.inflate
import com.bugtsa.casher.global.recycler.delegates.holders.BaseSwitchViewHolder
import com.bugtsa.casher.global.recycler.entities.ListItem
import com.bugtsa.casher.global.recycler.entities.SwitchItem
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate

class SwitchDelegateAdapter : AdapterDelegate<List<ListItem>>() {

    override fun onCreateViewHolder(parent: ViewGroup) = SwitchViewHolder(parent.inflate(R.layout.view_adapter_switch_item))

    override fun isForViewType(items: List<ListItem>, position: Int): Boolean = items[position] is SwitchItem

    override fun onBindViewHolder(items: List<ListItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        holder as SwitchViewHolder

        holder.bind(items[position])
    }

    class SwitchViewHolder(itemView: View) : BaseSwitchViewHolder(itemView) {
        override fun updateText(text: String?) {
            switch.text = text
        }
    }
}