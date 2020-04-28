package com.bugtsa.casher.ui.screens.purchases.show

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bugtsa.casher.R
import com.bugtsa.casher.data.network.PaymentsByDayRes
import com.bugtsa.casher.presentation.purchase.PurchasesViewModel
import com.bugtsa.casher.presentation.purchase.PurchasesViewModelFactory
import com.bugtsa.casher.ui.OnChangePosition
import com.bugtsa.casher.ui.adapters.PurchaseAdapter
import com.bugtsa.casher.ui.screens.purchases.add.AddPurchaseScreen
import kotlinx.android.synthetic.main.fragment_purchases.*
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.Finger
import pro.horovodovodo4ka.bones.extensions.present
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.ui.FingerNavigatorInterface
import pro.horovodovodo4ka.bones.ui.delegates.FingerNavigator
import toothpick.Toothpick

class PurchasesScreen(rootPhalanx: Bone? = null) : Finger(rootPhalanx) {

    override val seed = { PurchasesFragment() }
}

@SuppressLint("MissingSuperCall")
class PurchasesFragment : Fragment(R.layout.fragment_purchases), PurchasesView,
        FingerNavigatorInterface<PurchasesScreen> by FingerNavigator(R.id.payments_container),
        BonePersisterInterface<PurchasesScreen> {

    private lateinit var viewModel: PurchasesViewModel

    private lateinit var paymentsAdapter: PurchaseAdapter

    //region ================= Implements Methods =================

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        initPaymentsAdapter()
        initView()
        initViewModel()

        bindViewModel()
        viewModel.processData()

        refreshUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Toothpick.closeScope(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        managerProvider = ::getChildFragmentManager
    }

    override fun onDetach() {
        super.onDetach()
        managerProvider = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super<BonePersisterInterface>.onSaveInstanceState(outState)
        super<Fragment>.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<BonePersisterInterface>.onCreate(savedInstanceState)
        super<Fragment>.onCreate(savedInstanceState)
    }

    //endregion

    //region ================= Main View =================

    override fun showBottomScroll() {
        bottom_scroll.show()
    }

    override fun hideBottomScroll() {
        bottom_scroll.hide()
    }

    override fun scrollToPosition(position: Int) {
        purchases.scrollToPosition(position)
        bottom_scroll.isVisible = false
    }

    override fun setupPurchaseList(paymentsByDayList: List<PaymentsByDayRes>) {
        paymentsAdapter.setItems(paymentsByDayList)
        viewModel.requestScrollToDown()
    }

    override fun setupStatusText(status: String) {
        status_tv.text = status
        status_tv.isVisible = true
    }

    override fun showProgressBar(isVisible: Boolean) {
        setupStatusText("")
        progress_purchase.isVisible =  isVisible
    }

    override fun showPaymentList(isVisible: Boolean) {
        purchases.isVisible = isVisible
    }

    //endregion

    //region ================= Setup Ui =================

    private fun initView() {
        captions.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primaryDarkColor))
    }

    private fun showAddPurchaseController(): View.OnClickListener? {
        return View.OnClickListener {
            bone.present( AddPurchaseScreen())
        }
    }

    private fun requestToScrollDown(): View.OnClickListener? {
        return View.OnClickListener {
            viewModel.requestScrollToDown()
        }
    }

    private fun setupListeners() {
        add_purchase.setOnClickListener(showAddPurchaseController())
        bottom_scroll.setOnClickListener(requestToScrollDown())

        purchases.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 || dy < 0) {
                    viewModel.setScrollPurchasesList(true)
                }
            }
        })
    }

    private fun initPaymentsAdapter() {
        val linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        purchases.layoutManager = linearLayoutManager
        paymentsAdapter = PurchaseAdapter(object : OnChangePosition {
            override fun changePosition(position: Int) {
                viewModel.checkPositionAdapter(position)
            }
        })
        purchases.adapter = paymentsAdapter
    }

    //endregion

    private fun initViewModel() {
        val viewModelFactory = Toothpick
                .openScope(requireActivity().application)
                .getInstance(PurchasesViewModelFactory::class.java)
        viewModel = ViewModelProvider(this, viewModelFactory)[PurchasesViewModel::class.java]
    }

    private fun bindViewModel() {
        viewModel.observeProgress().observe(viewLifecycleOwner, Observer {
            showProgressBar(it)
        })
        viewModel.observeStatusText().observe(viewLifecycleOwner, Observer {
            setupStatusText(it)
        })
        viewModel.observePurchaseList().observe(viewLifecycleOwner, Observer {
            setupPurchaseList(it)
        })
        viewModel.observeScrollToPosition().observe(viewLifecycleOwner, Observer {
            scrollToPosition(it)
        })
        viewModel.observeBottomBarVisibility().observe(viewLifecycleOwner, Observer {
            if (it) showBottomScroll()
            else hideBottomScroll()
        })
    }
}