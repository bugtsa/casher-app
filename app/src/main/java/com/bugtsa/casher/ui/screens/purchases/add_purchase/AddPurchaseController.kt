package com.bugtsa.casher.ui.screens.purchases.add_purchase

import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bugtsa.casher.R
import com.bugtsa.casher.databinding.ControllerAddPurchaseBinding
import javax.inject.Inject

class AddPurchaseController : Controller(), AddPurchaseView {

    lateinit var binding: ControllerAddPurchaseBinding

    @Inject lateinit var presenter : AddPurchasePresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        var view: View = inflater.inflate(R.layout.controller_add_purchase, container, false)
        binding = DataBindingUtil.bind(view)

        presenter.onAttachView(this)

        return view
    }

    override fun onAttach(view: View) {
        super.onAttach(view)

        binding.savePurchase.setOnClickListener {
            presenter.addPurchase(binding.pricePurchaseEt.text.toString(),
                    binding.categoryPurchaseEt.text.toString())
            router.popCurrentController() }
        binding.cancelPurchase.setOnClickListener {router.popCurrentController()}
    }

}