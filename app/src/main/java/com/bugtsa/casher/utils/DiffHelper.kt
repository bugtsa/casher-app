package com.bugtsa.casher.utils

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

fun <T : DiffItem, R : RecyclerView.ViewHolder> RecyclerView.Adapter<R>.autoNotify(oldList: List<T>, newList: List<T>) {

    val diffItemCallback = object : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                oldList[oldItemPosition].getItemId() == newList[newItemPosition].getItemId()

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                oldList[oldItemPosition].getDiff() == newList[newItemPosition].getDiff()

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size
    }

    DiffUtil.calculateDiff(diffItemCallback).dispatchUpdatesTo(this)
}

interface DiffItem {
    fun getItemId(): String
    fun getDiff(): String
}