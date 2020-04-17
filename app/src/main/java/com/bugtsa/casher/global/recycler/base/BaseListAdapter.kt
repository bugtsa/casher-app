package com.bugtsa.casher.global.recycler.base

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bugtsa.casher.global.recycler.delegates.DataDelegateAdapter
import com.bugtsa.casher.global.recycler.delegates.DividerDelegateAdapter
import com.bugtsa.casher.global.recycler.delegates.LabelDelegateAdapter
import com.bugtsa.casher.global.recycler.delegates.SwitchDelegateAdapter
import com.bugtsa.casher.global.recycler.delegates.holders.BaseViewHolder
import com.bugtsa.casher.global.recycler.delegates.holders.SpaceDelegateAdapter
import com.bugtsa.casher.global.recycler.entities.ExpandableItem
import com.bugtsa.casher.global.recycler.entities.ListItem
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager


open class BaseListAdapter(
        callback: ((AdapterDelegatesManager<List<ListItem>>) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<ListItem>()

    protected val delegatesManager = AdapterDelegatesManager<List<ListItem>>()

    init {
        delegatesManager.addDelegate(LabelDelegateAdapter())
                .addDelegate(DividerDelegateAdapter())
                .addDelegate(SpaceDelegateAdapter())
                .addDelegate(DataDelegateAdapter())
                .addDelegate(SwitchDelegateAdapter())
        callback?.invoke(delegatesManager)
    }

    fun getItems(): List<ListItem> = items

    fun addOrReplaceFirstItem(item: ListItem) {
        if (items.isEmpty()) items.add(item)
        else if (items.size == 1) items[0] = item
        notifyItemChanged(0)
    }

    /**
     * Adds all [list] items to adapter from [position].
     */
    fun setItemsFrom(list: List<ListItem>, position: Int) {
        if (items.size < position) return

        val itemsClone = items.toList()
        items.clear()
        items.addAll(itemsClone.subList(0, position))
        items.addAll(list)

        DiffUtil.calculateDiff(ListDiffUtil(itemsClone, items))
                .dispatchUpdatesTo(this)
    }

    fun setItems(list: List<ListItem>) {
        val expandedList = expandElements(list)

        DiffUtil.calculateDiff(ListDiffUtil(items, expandedList))
                .dispatchUpdatesTo(this)
        items.clear()
        items.addAll(expandedList)
    }

    fun indexOf(id: Int) = getItems().indexOfFirst { it.id == id }

    fun setItem(index: Int, item: ListItem) {
        val list = items.toMutableList()
        list[index] = item
        setItems(list)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        delegatesManager.onViewRecycled(holder)
        if (holder is BaseViewHolder) {
            holder.unbind()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = delegatesManager.onCreateViewHolder(parent, viewType)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = delegatesManager.onBindViewHolder(items, position, holder)

    override fun getItemViewType(position: Int) = delegatesManager.getItemViewType(items, position)

    inner class ListDiffUtil(
            private val oldItems: List<ListItem>,
            private val newItems: List<ListItem>
    ) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                oldItems[oldItemPosition].javaClass.name == newItems[newItemPosition].javaClass.name

        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                oldItems[oldItemPosition] == newItems[newItemPosition]

    }

    private fun addItemsFrom(list: List<ListItem>, from: Int) {
        if (items.size < from) return

        val itemsClone = items.toList()

        items.addAll(from, list)

        DiffUtil.calculateDiff(ListDiffUtil(itemsClone, items))
                .dispatchUpdatesTo(this)
    }

    private fun removeItemsFrom(from: Int, size: Int) {
        val to = from + size - 1

        if (items.size < to) return

        val itemsClone = items.toList()
        (to downTo from).map { items.removeAt(it) }

        DiffUtil.calculateDiff(ListDiffUtil(itemsClone, items))
                .dispatchUpdatesTo(this)
    }

    private fun expandElements(elements: List<ListItem>) = elements.flatMap {
        when (it) {
            is ExpandableItem -> if (it.expandDefault) listOf(it) + it.expandElements() else listOf(it)
            else -> listOf(it)
        }
    }
}
