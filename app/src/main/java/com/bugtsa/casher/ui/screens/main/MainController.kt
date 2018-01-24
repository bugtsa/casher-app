package com.bugtsa.casher.ui.screens.main

import android.databinding.DataBindingUtil
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.bugtsa.casher.R
import com.bugtsa.casher.data.dto.PurchaseDto
import com.bugtsa.casher.databinding.ControllerMainBinding
import com.bugtsa.casher.ui.activities.RootActivity.Companion.REQUEST_AUTHORIZATION
import com.bugtsa.casher.ui.adapters.PurchaseAdapter
import com.bugtsa.casher.ui.screens.purchases.add_purchase.AddPurchaseController
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class MainController : Controller(), MainView {

    private lateinit var binding: ControllerMainBinding
    @Inject
    lateinit var presenter: MainPresenter

    lateinit private var mainControllerScope: Scope

    //region ================= Implements Methods =================

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        var view: View = inflater.inflate(R.layout.controller_main, container, false)

        binding = DataBindingUtil.bind(view)

        var linearLayoutManager = LinearLayoutManager(activity)
        binding.purchases.layoutManager = linearLayoutManager
        binding.addPurchase.setOnClickListener(showAddPurchaseController())

        mainControllerScope = Toothpick.openScopes(activity, this)
        Toothpick.inject(this, mainControllerScope)

        presenter.onAttachView(this)

        presenter.processData()
        return view
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        presenter.onViewDestroy()
        Toothpick.closeScope(this)
    }

    //endregion

    //region ================= Setup Ui =================

    private fun showAddPurchaseController(): View.OnClickListener? {
        return View.OnClickListener {
            router.pushController(RouterTransaction.with(AddPurchaseController()))
        }
    }

    //endregion

    //region ================= Main View =================

    override fun setupPurchaseList(purchaseList: MutableList<PurchaseDto>) {
        var purchaseAdapter = PurchaseAdapter(purchaseList)
        binding.purchases.adapter = purchaseAdapter
    }

    override fun setupStatusText(caption: String) {
        binding.statusTv.text = caption
        binding.statusTv.visibility = VISIBLE
    }

    override fun showProgressBar() {
        setupStatusText("")
        binding.progressPurchase.visibility = VISIBLE
    }

    override fun hideProgressBar() {
        binding.progressPurchase.visibility = GONE
    }

    override fun startIntent(lastError: Exception?) {
        startActivityForResult(
                (lastError as UserRecoverableAuthIOException).intent,
                REQUEST_AUTHORIZATION)
    }
    //endregion

}