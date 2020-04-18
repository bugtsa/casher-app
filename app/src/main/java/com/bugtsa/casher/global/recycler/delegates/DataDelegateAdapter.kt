package com.bugtsa.casher.global.recycler.delegates

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bugtsa.casher.R
import com.bugtsa.casher.global.extentions.inflate
import com.bugtsa.casher.global.recycler.entities.DataItem
import com.bugtsa.casher.global.recycler.entities.ListItem
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate

class DataDelegateAdapter : AdapterDelegate<List<ListItem>>() {

    override fun onCreateViewHolder(parent: ViewGroup) = DataViewHolder(parent.inflate(R.layout.view_adapter_data_item))

    override fun isForViewType(items: List<ListItem>, position: Int): Boolean = items[position] is DataItem

    override fun onBindViewHolder(items: List<ListItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        val layout = (holder as DataViewHolder)
        val item = items[position] as DataItem

        with(layout) {
            @SuppressLint("NewApi")
            rootLay.background = ContextCompat.getDrawable(label.context, item.backgroundRes)

            if (item.textLabel != null) {
                label.isVisible = true
                label.text = item.textLabel
            } else {
                label.isVisible = false
            }

            data.text = item.textData
            data.setTextColor(ContextCompat.getColor(itemView.context, item.dataTextColor))
            label.setTextColor(ContextCompat.getColor(label.context, item.labelTextColor))

            if (item.iconRes != null) {
                imageIcon.isVisible = true
                imageIcon.setImageResource(item.iconRes)
            } else {
                imageIcon.isVisible = false
            }
        }
    }

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val label: TextView = itemView.findViewById(R.id.vHolderLabel)
        val data: TextView = itemView.findViewById(R.id.vHolderData)
        val rootLay: ConstraintLayout = itemView.findViewById(R.id.vRootLayData)

        val imageIcon: ImageView = itemView.findViewById(R.id.vImageData)
    }
}