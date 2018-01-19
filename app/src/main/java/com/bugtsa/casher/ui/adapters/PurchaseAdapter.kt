package com.bugtsa.casher.ui.adapters

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import com.bugtsa.casher.R
import com.bugtsa.casher.data.dto.PurchaseDto
import kotlinx.android.synthetic.main.item_purchase.view.*

class PurchaseAdapter(purchaseList: MutableList<PurchaseDto>) : RecyclerView.Adapter<PurchaseAdapter.ViewHolder>() {

    var purchaseList: MutableList<PurchaseDto> = purchaseList
    var dateMap: MutableMap<String, Int> = mutableMapOf()

    //region ================= Implements Methods =================

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        var purchase: PurchaseDto = purchaseList[position]
        if (!TextUtils.isEmpty(purchase.date) && !dateMap.contains(purchase.date)) {
            dateMap.put(purchase.date, position)
            showDateTitle(holder, purchase.date)
        } else if (dateMap.contains(purchase.date) && dateMap.get(purchase.date) == position) {
            showDateTitle(holder, purchase.date)
        } else {
            holder?.date?.visibility = GONE
        }
        holder?.timePurchase?.text = purchase.time
        holder?.price?.text = purchase.price
        holder?.category?.text = purchase.category
    }

    override fun getItemCount(): Int {
        return purchaseList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_purchase, parent, false))
    }

    //endregion

    //region ================= View Holder =================

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
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