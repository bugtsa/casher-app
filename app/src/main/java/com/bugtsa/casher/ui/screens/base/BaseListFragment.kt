package com.bugtsa.casher.ui.screens.base

import android.os.Bundle
import android.view.View
import com.bugtsa.casher.global.recycler.base.ListAdapter
import com.bugtsa.casher.global.recycler.layoutmanager.AppBarRecyclerViewLayoutManager
import kotlinx.android.synthetic.main.fragment_delegeted_adapter_list.*

abstract class BaseListFragment : BaseFragment() {

    override val layout: Int = com.bugtsa.casher.R.layout.fragment_delegeted_adapter_list

    open val adapter: ListAdapter by lazy {
        ListAdapter()
    }

    private var clickSupport: ItemClickSupport? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vDelegatedListView.layoutManager = AppBarRecyclerViewLayoutManager(homeActivity)
        vDelegatedListView.adapter = adapter
        vDelegatedListView.itemAnimator = null

        onItemsAddedToList()

        clickSupport = ItemClickSupport.addTo(vDelegatedListView)
                .setOnItemClickListener(object : ItemClickSupport.OnItemClickListener {
                    override fun onItemClicked(v: View, position: Int) {
                        onListItemClick(v, position)
                    }
                })
                .setOnItemLongClickListener(object : ItemClickSupport.OnItemLongClickListener {
                    override fun onItemLongClicked(v: View, position: Int): Boolean {
                        onListItemLongClick(v, position)
                        return true
                    }

                })
    }

    abstract fun onListItemClick(v: View, position: Int)

    /**
     * To handle Long clicks just override this method in children classes
     * */
    open fun onListItemLongClick(v: View, position: Int) {}

    abstract fun onItemsAddedToList()

    override fun onDestroyView() {
        vDelegatedListView?.adapter = null
        clickSupport?.detach(vDelegatedListView)
        clickSupport = null
        super.onDestroyView()
    }
}