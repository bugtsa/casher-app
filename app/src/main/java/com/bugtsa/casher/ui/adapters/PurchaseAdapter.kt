package com.bugtsa.casher.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import com.bugtsa.casher.R
import com.bugtsa.casher.data.dto.PaymentDto
import com.bugtsa.casher.data.network.payment.PaymentsByDayRes
import com.bugtsa.casher.ui.OnChangePosition
import com.bugtsa.casher.utils.autoNotify
import kotlinx.android.synthetic.main.item_payment_caption.view.*
import kotlinx.android.synthetic.main.item_purchase.view.*
import kotlin.properties.Delegates

class PurchaseAdapter(private val onChangePosition: OnChangePosition)
    : androidx.recyclerview.widget.RecyclerView.Adapter<PurchaseAdapter.ViewHolder>() {

    private var paymentsByDayList: List<PaymentsByDayRes> by Delegates.observable(initialValue = listOf(), onChange = { property, oldValue, newValue ->
        autoNotify(oldValue, newValue)
    })

    //region ================= Implements Methods =================

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val paymentByDay: PaymentsByDayRes = paymentsByDayList[position]
        when {
            paymentByDay.date?.isNotEmpty() == true -> showDateTitle(holder, paymentByDay.date)
            paymentByDay.payment != null -> {
                showPayment(holder, paymentByDay.payment)
            }
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

    fun setItems(itemList: List<PaymentsByDayRes>) {
        this.paymentsByDayList = itemList
    }

    //region ================= View Holder =================

    class ViewHolder(item: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(item) {
        var date: TextView = item.date_purchase
        var timePurchase: TextView = item.payment_item.time_purchase
        var cost: TextView = item.payment_item.cost_purchase
        var modernBalance: TextView = item.payment_item.balance_caption_payment
        var category: TextView = item.payment_item.category_purchase
    }

    //endregion

    //region ================= private Functions =================

    private fun showPayment(holder: ViewHolder?, payment: PaymentDto) {
        holder?.date?.isVisible = false

        setupPaymentVisibility(holder, true)
        holder?.timePurchase?.text = payment.time
        holder?.cost?.text = payment.cost
        holder?.modernBalance?.text = if (payment.balance.isNotEmpty()) payment.balance else ""
        holder?.category?.text = payment.category
    }

    private fun showDateTitle(holder: ViewHolder?, date: String) {
        holder?.date?.isVisible = true
        holder?.date?.text = date

        setupPaymentVisibility(holder, false)
    }

    private fun setupPaymentVisibility(holder: ViewHolder?, visibility: Boolean) {
        holder?.category?.isVisible = visibility
        holder?.cost?.isVisible = visibility
        holder?.modernBalance?.isVisible = visibility
        holder?.timePurchase?.isVisible = visibility
    }
    //endregion

}