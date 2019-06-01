package com.bugtsa.casher.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import com.bugtsa.casher.R
import com.bugtsa.casher.data.dto.PurchaseDto
import com.bugtsa.casher.ui.OnChangePosition
import kotlinx.android.synthetic.main.item_purchase.view.*

class PurchaseAdapter(purchaseList: MutableList<PurchaseDto>,
                      dateMap: MutableMap<String, Int>,
                      onChangePosition: OnChangePosition)
    : androidx.recyclerview.widget.RecyclerView.Adapter<PurchaseAdapter.ViewHolder>() {

    var purchasesList: MutableList<PurchaseDto> = purchaseList
    var datesMap: MutableMap<String, Int> = dateMap
    var onChangePosition: OnChangePosition = onChangePosition

    //region ================= Implements Methods =================

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val purchase: PurchaseDto = purchasesList[position]
        if (datesMap.contains(purchase.date) && datesMap.get(purchase.date) == position) {
            showDateTitle(holder, purchase.date)
        } else {
            holder.date.visibility = GONE
        }
        holder.timePurchase.text = purchase.time
        holder.price.text = purchase.price
        holder.category.text = purchase.category
        onChangePosition.changePosition(holder.layoutPosition)
    }

    override fun getItemCount(): Int {
        return purchasesList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_purchase, parent, false))
    }

    //endregion

    //region ================= View Holder =================

    class ViewHolder(item: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(item) {
        var date: TextView = item.date_purchase
        var timePurchase: TextView = item.time_purchase
        var price: TextView = item.price_purchase
        var category: TextView = item.category_purchase
    }

    //endregion

    //region ================= private Functions =================

    private fun showDateTitle(holder: ViewHolder?, date: String) {
        holder?.date?.visibility = VISIBLE
        holder?.date?.text = date
    }

    //endregion

}