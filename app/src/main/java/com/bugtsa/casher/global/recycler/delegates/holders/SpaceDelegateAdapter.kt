package com.bugtsa.casher.global.recycler.delegates.holders

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bugtsa.casher.R
import com.bugtsa.casher.global.extentions.convertDpToPx
import com.bugtsa.casher.global.extentions.inflate
import com.bugtsa.casher.global.recycler.entities.ListItem
import com.bugtsa.casher.global.recycler.entities.SpaceItem
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate

class SpaceDelegateAdapter  : AdapterDelegate<List<ListItem>>() {

    companion object {
        private const val DEFAULT_HEIGHT_PX = 24
    }

    override fun onCreateViewHolder(parent: ViewGroup) = SpaceViewHolder(parent.inflate(R.layout.view_adapter_space_item))

    override fun isForViewType(items: List<ListItem>, position: Int): Boolean = items[position] is SpaceItem

    override fun onBindViewHolder(items: List<ListItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        with((holder as SpaceViewHolder).view) {
            val item = items[position] as SpaceItem
            val height = context?.convertDpToPx(item.height) ?: DEFAULT_HEIGHT_PX
            layoutParams.height = height
            setBackgroundColor(ContextCompat.getColor(context, item.backgroundColor))
        }
    }

    class SpaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view: View = itemView.findViewById(R.id.vContainer)
    }
}