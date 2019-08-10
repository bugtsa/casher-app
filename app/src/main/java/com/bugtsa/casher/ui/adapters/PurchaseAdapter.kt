package com.bugtsa.casher.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import com.bugtsa.casher.R
import com.bugtsa.casher.data.dto.PaymentDto
import com.bugtsa.casher.ui.OnChangePosition
import kotlinx.android.synthetic.main.item_purchase.view.*

class PurchaseAdapter(paymentList: MutableList<PaymentDto>,
                      dateMap: MutableMap<String, Int>,
                      onChangePosition: OnChangePosition)
    : androidx.recyclerview.widget.RecyclerView.Adapter<PurchaseAdapter.ViewHolder>() {

    var purchasesList: MutableList<PaymentDto> = paymentList
    var datesMap: MutableMap<String, Int> = dateMap
    var onChangePosition: OnChangePosition = onChangePosition

    //region ================= Implements Methods =================

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val payment: PaymentDto = purchasesList[position]
        if (datesMap.contains(payment.date) && datesMap.get(payment.date) == position) {
            showDateTitle(holder, payment.date)
        } else {
            holder.date.visibility = GONE
        }
        holder.timePurchase.text = payment.time
        holder.price.text = payment.price
        holder.category.text = payment.category
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