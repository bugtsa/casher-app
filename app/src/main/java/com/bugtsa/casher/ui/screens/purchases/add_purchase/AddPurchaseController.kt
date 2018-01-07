package com.bugtsa.casher.ui.screens.purchases.add_purchase

import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bugtsa.casher.R
import com.bugtsa.casher.databinding.ControllerAddPurchaseBinding
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class AddPurchaseController : Controller(), AddPurchaseView {

    lateinit var binding: ControllerAddPurchaseBinding

    @Inject lateinit var presenter: AddPurchasePresenter

    lateinit private var addPurchaseScope: Scope

    //region ================= Implements Methods =================

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        var view: View = inflater.inflate(R.layout.controller_add_purchase, container, false)
        binding = DataBindingUtil.bind(view)

        addPurchaseScope = Toothpick.openScopes(activity, this)
        Toothpick.inject(this, addPurchaseScope)
        presenter.onAttachView(this)

        return view
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        presenter.onViewDestroy()
    }

    override fun onAttach(view: View) {
        super.onAttach(view)

        binding.savePurchase.setOnClickListener {
            presenter.addPurchase(binding.pricePurchaseEt.text.toString(),
                    binding.categoryPurchaseEt.text.toString())
        }
        binding.cancelPurchase.setOnClickListener { popCurrentController() }
    }

    //endregion

    private fun popCurrentController() {
        router.popCurrentController()
    }

    //region ================= Add Purchase View =================

    override fun completedAddPurchase() {
        popCurrentController()
    }

    override fun showProgressBar() {
//        binding.pro
    }

    override fun hideProgressBar() {
//        binding.pro
    }

    //endregion


}