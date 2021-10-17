package com.bugtsa.casher.ui.screens.purchases.add

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bugtsa.casher.R
import com.bugtsa.casher.presentation.purchase.AddPurchaseViewModel
import com.bugtsa.casher.presentation.purchase.AddPurchaseViewModelFactory
import com.bugtsa.casher.ui.screens.base.BaseFragment
import com.bugtsa.casher.utils.DateConverter.toShortDateString
import com.bugtsa.casher.utils.getDefaultCalendar
import com.bugtsa.casher.utils.showDateDialog
import com.bugtsa.casher.utils.showTimeDialog
import kotlinx.android.synthetic.main.fragment_add_purchase.*
import pro.horovodovodo4ka.bones.Phalanx
import pro.horovodovodo4ka.bones.extensions.dismiss
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.ui.FragmentSibling
import pro.horovodovodo4ka.bones.ui.delegates.Page
import pro.horovodovodo4ka.bones.ui.extensions.addNavigationToToolbar
import toothpick.Toothpick
import java.util.*

interface AddPaymentStackPresentable {
    val fragmentTitle: String
}

open class AddPurchaseScreen(private val updateSubscriptions: () -> Unit) : Phalanx() {
    override val seed = { AddPurchaseFragment() }

    override fun onOrphaned() {
        super.onOrphaned()
        updateSubscriptions.invoke()
    }
}

@Suppress("DEPRECATED_IDENTITY_EQUALS")
@SuppressLint("MissingSuperCall")
class AddPurchaseFragment : BaseFragment(), AddPurchaseView,
    FragmentSibling<AddPurchaseScreen> by Page(),
    BonePersisterInterface<AddPurchaseScreen>,
    AddPaymentStackPresentable {

    private lateinit var viewModel: AddPurchaseViewModel

    //region ================= Implements Methods =================

    override val layout: Int
        get() = R.layout.fragment_add_purchase

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = Toothpick
            .openScopes(activity, this)
            .getInstance(AddPurchaseViewModelFactory::class.java)
        viewModel = ViewModelProvider(this, viewModelFactory)[AddPurchaseViewModel::class.java]

        bindListeners()
        bindViewModel()

        onRefresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onViewDestroy()
        Toothpick.closeScope(this)
    }

    //endregion

    override fun onSaveInstanceState(outState: Bundle) {
        super<BonePersisterInterface>.onSaveInstanceState(outState)
        super<BaseFragment>.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<BonePersisterInterface>.onCreate(savedInstanceState)
        super<BaseFragment>.onCreate(savedInstanceState)
    }

    override val fragmentTitle: String
        get() = getString(R.string.screen_add_purchase)

    override fun onRefresh() {
        if (view == null) return

        val title = fragmentTitle
        toolbar.visibility = View.VISIBLE
        toolbar.title = title
        addNavigationToToolbar(toolbar, R.drawable.ic_arrow_back_white)
        toolbar.setNavigationOnClickListener { completedAddPurchase() }
    }

    //region ================= Add Purchase View =================

    override fun completedAddPurchase() = bone.dismiss()

    override fun showProgressBar() {
        showProgress(cancelAction = { processBackPress() })
    }

    override fun hideProgressBar() {
        hideProgress()
    }

    override fun setupCategoriesList(categoriesList: List<String>) {
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            categoriesList
        )
        category_purchase_et.setAdapter(adapter)
    }

    @SuppressLint("SetTextI18n")
    override fun setupCurrentDateAndTime(dateAndTime: String) {
        date_purchase.text =
            requireContext().getString(R.string.current_date_and_time) + dateAndTime
    }

    //endregion

    //region ================= Calendar And Time Picker =================

    override fun showDatePicker() {
        val calendar = getDefaultCalendar()
        calendar.time = Date()
        val positiveColorId = R.color.colorAccent
        val negativeColorId = R.color.secondaryColor
        requireContext().showDateDialog(
            calendar,
            positiveColorId,
            negativeColorId
        ) { yearMonthDay ->
            requireContext().showTimeDialog(
                yearMonthDay,
                R.string.order_info_time_start,
                positiveColorId,
                negativeColorId
            ) { date ->
                val stringDate = date.time.toShortDateString()
                viewModel.changeDate(stringDate)
            }
        }
    }

    //endregion

    private fun bindViewModel() {
        viewModel.requestSetupCurrentDate()
        viewModel.checkExistCategoriesInDatabase()

        viewModel.observeCategoriesList().observe(viewLifecycleOwner,
            Observer { categoriesList ->
                setupCategoriesList(categoriesList)
            })
        viewModel.observeCompleteAddPayment().observe(viewLifecycleOwner,
            Observer {
                completedAddPurchase()
            })
        viewModel.observeSetupCurrentDate().observe(viewLifecycleOwner,
            Observer { currentDateAndTime ->
                setupCurrentDateAndTime(currentDateAndTime)
            })
        viewModel.isAddDateCheckboxActivated.observe(viewLifecycleOwner,
            Observer { isChecked ->
                add_date_purchase.isChecked = isChecked
            })
        viewModel.observeShowDatePicker().observe(viewLifecycleOwner,
            Observer {
                showDatePicker()
            })
        viewModel.observeShowProgress().observe(viewLifecycleOwner,
            Observer { isShow ->
                if (isShow) showProgressBar() else hideProgressBar()
            })
    }

    private fun bindListeners() {
        setupCategoriesTouchListener()
        add_date_purchase.setOnClickListener {
            viewModel.checkSetupCustomDateAndTime(add_date_purchase.isChecked)
        }

        save_purchase.setOnClickListener {
            viewModel.checkCategorySaveOnDatabase(
                price_purchase_et.text.toString(),
                category_purchase_et.text.toString()
            )
        }
        cancel_purchase.setOnClickListener { completedAddPurchase() }
    }

    //region ================= Categories List Methods =================

    private fun setupCategoriesTouchListener() {
        category_purchase_et.setOnTouchListener { _, event ->
            if (event.action === MotionEvent.ACTION_UP) {
                if (event.rawX >= category_purchase_et.right - category_purchase_et.totalPaddingRight) {
                    resetSearchView()
                }
            }
            false
        }
    }

    private fun resetSearchView() {
        category_purchase_et.text.clear()
    }

    //endregion

}