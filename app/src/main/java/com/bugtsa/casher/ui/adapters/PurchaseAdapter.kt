package com.bugtsa.casher.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import com.bugtsa.casher.R
import com.bugtsa.casher.data.dto.PaymentsByDayRes
import com.bugtsa.casher.ui.OnChangePosition
import kotlinx.android.synthetic.main.item_purchase.view.*

class PurchaseAdapter(val paymentsByDayList: List<PaymentsByDayRes>,
                      val onChangePosition: OnChangePosition)
    : androidx.recyclerview.widget.RecyclerView.Adapter<PurchaseAdapter.ViewHolder>() {

    //region ================= Implements Methods =================

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val paymentByDay: PaymentsByDayRes = paymentsByDayList[position]
        if (paymentByDay.datePayments?.isNotEmpty() == true) {
            showDateTitle(holder, paymentByDay.datePayments)
        } else if (paymentByDay.payment != null) {
            val payment = paymentByDay.payment
            holder.date.visibility = GONE
            holder.timePurchase.text = payment.time
            holder.cost.text = payment.cost
            if (payment.balance.isNotEmpty()) {
                holder.balance.text = payment.balance
                holder.balance.visibility = VISIBLE
            }
            holder.category.text = payment.category
        }
        onChangePosition.changePosition(holder.layoutPosition)
    }

    override fun getItemCount(): Int {
        return paymentsByDayList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_purchase, parent, false))
    }

    //endregion

    //region ================= View Holder =================

    class ViewHolder(item: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(item) {
        var date: TextView = item.date_purchase
        var timePurchase: TextView = item.time_purchase
        var cost: TextView = item.cost_purchase
        var balance: TextView = item.balance_purchase
        var category: TextView = item.category_purchase
    }

    //endregion

    //region ================= private Functions =================

    private fun showDateTitle(holder: ViewHolder?, date: String) {
        holder?.date?.visibility = VISIBLE
        holder?.date?.text = date
        holder?.balance?.visibility = GONE
        holder?.category?.visibility = GONE
        holder?.cost?.visibility = GONE
        holder?.timePurchase?.visibility = GONE
    }

    //endregion

}