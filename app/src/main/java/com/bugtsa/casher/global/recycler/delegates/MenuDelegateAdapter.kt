package com.bugtsa.casher.global.recycler.delegates

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bugtsa.casher.R
import com.bugtsa.casher.global.extentions.inflate
import com.bugtsa.casher.global.recycler.delegates.holders.BaseViewHolder
import com.bugtsa.casher.global.recycler.entities.ListItem
import com.bugtsa.casher.global.recycler.entities.MenuItem
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate

class MenuDelegateAdapter() : AdapterDelegate<List<ListItem>>(), Parcelable {

    constructor(parcel: Parcel) : this() {
    }

    override fun onCreateViewHolder(parent: ViewGroup) = MenuViewHolder(parent.inflate(R.layout.view_adapter_list_item))

    override fun isForViewType(items: List<ListItem>, position: Int): Boolean = items[position] is MenuItem

    override fun onBindViewHolder(items: List<ListItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        holder as MenuViewHolder

        holder.bind(items[position])
    }

    class MenuViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.vHolderItemTitle)
        private val subTitle: TextView = itemView.findViewById(R.id.vHolderItemSubTitle)
        private val icon: ImageView = itemView.findViewById(R.id.vHolderIcon)
        private val counter: TextView = itemView.findViewById(R.id.vHolderCounter)
        private val arrow: ImageView = itemView.findViewById(R.id.vHolderArrow)

        private var visibleLiveData: LiveData<Boolean>? = null
        private lateinit var visibleObserver: Observer<Boolean>
        private var subtitleResIdLiveData: LiveData<Int>? = null
        private lateinit var subTitleObserver: Observer<Int>

        override fun unbind() {
            visibleLiveData?.removeObserver(visibleObserver)
            subtitleResIdLiveData?.removeObserver(subTitleObserver)
            super.unbind()
        }

        override fun bind(item: ListItem) {
            super.bind(item)

            item as MenuItem

            title.text = item.title
            title.setTextColor(ContextCompat.getColor(title.context, item.textColor))

            icon.isVisible = item.icon != null

            // set drawable to image here
            Glide.with(icon.context)
                    .load(item.icon)
                    .into(icon)

            counter.isVisible = item.counter > 0
            counter.also {
                it.text = item.counter.toString()
            }

            arrow.isVisible = item.isArrowEnabled

            counter.background = ContextCompat.getDrawable(counter.context, item.counterBackground)
            counter.setTextColor(ContextCompat.getColor(counter.context, item.counterTextColor))

            itemView.isVisible = item.initiallyVisible
            visibleLiveData = item.visible
            visibleObserver = Observer {
                itemView.isVisible = it
            }

            visibleLiveData?.observe((itemView.context as LifecycleOwner), visibleObserver)

            subtitleResIdLiveData = item.subTitleLiveDataResId
            subTitleObserver = Observer {
                val subtitleText = itemView.resources.getString(it)
                if (!TextUtils.isEmpty(subtitleText)) {
                    subTitle.visibility = View.VISIBLE
                    subTitle.text = subtitleText
                }
            }

            subtitleResIdLiveData?.observe((itemView.context as LifecycleOwner), subTitleObserver)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MenuDelegateAdapter> {
        override fun createFromParcel(parcel: Parcel): MenuDelegateAdapter {
            return MenuDelegateAdapter(parcel)
        }

        override fun newArray(size: Int): Array<MenuDelegateAdapter?> {
            return arrayOfNulls(size)
        }
    }
}