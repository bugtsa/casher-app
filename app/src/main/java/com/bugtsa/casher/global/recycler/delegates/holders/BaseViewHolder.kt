package com.bugtsa.casher.global.recycler.delegates.holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bugtsa.casher.global.recycler.entities.ListItem

abstract class BaseViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
    open fun bind(item: ListItem) {}
    open fun unbind() {}
}