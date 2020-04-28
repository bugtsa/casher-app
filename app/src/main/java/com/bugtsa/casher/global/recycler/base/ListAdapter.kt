package com.bugtsa.casher.global.recycler.base

import com.bugtsa.casher.global.recycler.entities.ListItem
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager

class ListAdapter(callback: ((AdapterDelegatesManager<List<ListItem>>) -> Unit)? = null) : BaseListAdapter(callback)
