package com.bugtsa.casher.global.recycler.delegates

import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.recyclerview.widget.RecyclerView
import com.bugtsa.casher.R
import com.bugtsa.casher.global.extentions.convertDpToPx
import com.bugtsa.casher.global.extentions.inflate
import com.bugtsa.casher.global.recycler.entities.LabelItem
import com.bugtsa.casher.global.recycler.entities.ListItem
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate

class LabelDelegateAdapter : AdapterDelegate<List<ListItem>>() {

    override fun onCreateViewHolder(parent: ViewGroup) = LabelViewHolder(parent.inflate(R.layout.view_adapter_label_item))

    override fun isForViewType(items: List<ListItem>, position: Int): Boolean = items[position] is LabelItem

    override fun onBindViewHolder(items: List<ListItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        val layout = (holder as LabelViewHolder)
        val item = items[position] as LabelItem

        with(layout) {
            val topBottomMargins = vLabel.context.convertDpToPx(item.topBottomMargins)

            vLabel.updateLayoutParams<ViewGroup.MarginLayoutParams> { updateMargins(top = topBottomMargins, bottom = topBottomMargins) }

            vLabel.text = item.textLabel
            vLabel.isAllCaps = item.isAllCaps
            vLabel.setTextSize(COMPLEX_UNIT_SP, item.textSize.toFloat())
            vLabel.setTextColor(ContextCompat.getColor(vLabel.context, item.textColor))
            item.background?.let {
                vRoot.setBackgroundResource(item.background)
            } ?: run {
                vRoot.setBackgroundColor(ContextCompat.getColor(vRoot.context, item.backgroundColor))
            }
        }
    }

    class LabelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vLabel: TextView = itemView.findViewById(R.id.vHolderLabel)
        val vRoot: View = itemView.findViewById(R.id.vRoot)
    }
}