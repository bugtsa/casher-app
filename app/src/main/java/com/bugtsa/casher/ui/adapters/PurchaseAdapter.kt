package com.bugtsa.casher.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bugtsa.casher.R
import com.bugtsa.casher.data.dto.PurchaseDto
import kotlinx.android.synthetic.main.item_purchase.view.*

class PurchaseAdapter(purchaseList: MutableList<PurchaseDto>) : RecyclerView.Adapter<PurchaseAdapter.ViewHolder>() {

    var purchaseList: MutableList<PurchaseDto> = purchaseList

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        var purchase: PurchaseDto = purchaseList[position]
        holder?.date?.text = purchase.date
        holder?.timePurchase?.text = purchase.date
        holder?.price?.text = purchase.price
        holder?.category?.text = purchase.category
    }

    override fun getItemCount(): Int {
        return purchaseList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_purchase, parent, false))
    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        var date: TextView = item.date_purchase
        var timePurchase: TextView = item.time_purchase
        var price: TextView = item.price_purchase
        var category: TextView = item.category_purchase
    }
}