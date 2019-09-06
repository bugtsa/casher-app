package com.bugtsa.casher.ui.screens.purchases.show

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.bugtsa.casher.R
import com.bugtsa.casher.data.dto.PaymentsByDayRes
import com.bugtsa.casher.ui.OnChangePosition
import com.bugtsa.casher.ui.adapters.PurchaseAdapter
import com.bugtsa.casher.ui.screens.purchases.add.AddPurchaseScreen
import com.bugtsa.casher.utils.visibility
import kotlinx.android.synthetic.main.fragment_purchases.*
import pro.horovodovodo4ka.bones.Finger
import pro.horovodovodo4ka.bones.Phalanx
import pro.horovodovodo4ka.bones.extensions.closest
import pro.horovodovodo4ka.bones.extensions.present
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.ui.ScreenInterface
import pro.horovodovodo4ka.bones.ui.delegates.Page
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject


class PurchasesScreen : Phalanx() {

    fun showRootView() {
        val bot = closest<Finger>()?.phalanxes?.first()
    }

    override val seed = { PurchasesFragment() }
}

@SuppressLint("MissingSuperCall")
class PurchasesFragment : Fragment(), PurchasesView,
        ScreenInterface<PurchasesScreen> by Page(), BonePersisterInterface<PurchasesScreen> {

    @Inject
    lateinit var presenter: PurchasesPresenter

    private lateinit var mainControllerScope: Scope

    //region ================= Implements Methods =================

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_purchases, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        purchases.layoutManager = linearLayoutManager
        setupScrollListener()

        add_purchase.setOnClickListener(showAddPurchaseController())
        bottom_scroll.setOnClickListener(requestToScrollDown())

        mainControllerScope = Toothpick.openScopes(activity, this)
        Toothpick.inject(this, mainControllerScope)

        presenter.onAttachView(this)
        presenter.processData()

        refreshUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onViewDestroy()
        Toothpick.closeScope(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super<BonePersisterInterface>.onSaveInstanceState(outState)
        super<androidx.fragment.app.Fragment>.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<BonePersisterInterface>.onCreate(savedInstanceState)
        super<androidx.fragment.app.Fragment>.onCreate(savedInstanceState)
    }

    //endregion

    //region ================= Setup Ui =================

    private fun showAddPurchaseController(): View.OnClickListener? {
        return View.OnClickListener {
            bone.present(AddPurchaseScreen())
        }
    }

    private fun requestToScrollDown(): View.OnClickListener? {
        return View.OnClickListener {
            presenter.requestScrollToDown()
        }
    }

    private fun setupScrollListener() {
        purchases.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 || dy < 0) {
                    presenter.setScrollPurchasesList(true)
                }
            }
        })
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
        bottom_scroll.visibility { false }
    }

    override fun setupPurchaseList(paymentsByDayList: List<PaymentsByDayRes>) {
        val purchaseAdapter = PurchaseAdapter(paymentsByDayList, object : OnChangePosition {
            override fun changePosition(position: Int) {
                presenter.checkPositionAdapter(position)
            }
        })
        purchases.adapter = purchaseAdapter
        presenter.requestScrollToDown()
    }

    override fun setupStatusText(caption: String) {
        status_tv.text = caption
        status_tv.visibility { true }
    }

    override fun showProgressBar(isVisible: Boolean) {
        setupStatusText("")
        progress_purchase.visibility { isVisible }
    }

    override fun showPaymentList(isVisible: Boolean) {
        purchases.visibility { isVisible }
    }

    //endregion
}