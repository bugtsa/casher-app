package com.bugtsa.casher.ui.screens.purchases.add_purchase

import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bugtsa.casher.R
import com.bugtsa.casher.databinding.ControllerAddPurchaseBinding

class AddPurchaseController : Controller() , AddPurchaseView {

    lateinit var binding : ControllerAddPurchaseBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        var view : View = inflater.inflate(R.layout.controller_add_purchase, container, false)
        binding = DataBindingUtil.bind(view)

        return view
    }
}