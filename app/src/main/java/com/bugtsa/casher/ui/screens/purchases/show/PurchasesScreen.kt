package com.bugtsa.casher.ui.screens.purchases.show

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bugtsa.casher.R
import com.bugtsa.casher.data.dto.PurchaseDto
import com.bugtsa.casher.ui.OnChangePosition
import com.bugtsa.casher.ui.adapters.PurchaseAdapter
import kotlinx.android.synthetic.main.controller_main.*
import pro.horovodovodo4ka.bones.Finger
import pro.horovodovodo4ka.bones.Phalanx
import pro.horovodovodo4ka.bones.extensions.closest
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.ui.ScreenInterface
import pro.horovodovodo4ka.bones.ui.delegates.Page
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject


class PurchasesScreen : Phalanx() {

	fun showRootView() {
		val bot = closest<Finger>()?.phalanxes?.first()
//        val dlg = RootSpineBone(TabBarBone(NavigationStackBone(CardListBone()),
//                ProfileNavigationStack(),
//                NavigationStackBone(UploadPhotoBone())))
//        closest<Finger>()?.replace(bot, dlg)
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
		return inflater.inflate(R.layout.controller_main, container, false)
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
			//            router.pushController(RouterTransaction.with(AddPurchaseController()))
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
			}

//            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
//                if (dy > 0 || dy < 0) {
//                    presenter.setScrollPurchasesList(true)
//                }
//            }
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
		bottom_scroll.visibility = GONE
	}

	override fun setupPurchaseList(
		purchaseList: MutableList<PurchaseDto>,
		dateMap: MutableMap<String, Int>
	) {
		val purchaseAdapter = PurchaseAdapter(purchaseList, dateMap, object : OnChangePosition {
			override fun changePosition(position: Int) {
				presenter.checkPositionAdapter(position)
			}
		})
		purchases.adapter = purchaseAdapter
		presenter.requestScrollToDown()
	}

	override fun setupStatusText(caption: String) {
		status_tv.text = caption
		status_tv.visibility = VISIBLE
	}

	override fun showProgressBar() {
		setupStatusText("")
		progress_purchase.visibility = VISIBLE
	}

	override fun hideProgressBar() {
		progress_purchase.visibility = GONE
	}

	override fun startIntent(lastError: Exception?) {
//        startActivityForResult(
//                (lastError as UserRecoverableAuthIOException).intent,
//                REQUEST_AUTHORIZATION)
	}
	//endregion
}