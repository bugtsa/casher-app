package com.bugtsa.casher.ui.screens.purchases.add

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.borax12.materialdaterangepicker.time.RadialPickerLayout
import com.borax12.materialdaterangepicker.time.TimePickerDialog
import com.bugtsa.casher.R
import com.bugtsa.casher.ui.screens.BaseFragment
import com.maxproj.calendarpicker.Builder
import kotlinx.android.synthetic.main.fragment_add_purchase.*
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.Finger
import pro.horovodovodo4ka.bones.extensions.dismiss
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.ui.FingerNavigatorInterface
import pro.horovodovodo4ka.bones.ui.delegates.FingerNavigator
import pro.horovodovodo4ka.bones.ui.extensions.addNavigationToToolbar
import toothpick.Toothpick
import java.util.*


interface AddPaymentStackPresentable {
    val fragmentTitle: String
}

open class AddPurchaseScreen(rootPhalanx: Bone? = null) : Finger(rootPhalanx) {
    override val seed = { AddPurchaseFragment() }
}

@Suppress("DEPRECATED_IDENTITY_EQUALS")
@SuppressLint("MissingSuperCall")
class AddPurchaseFragment : BaseFragment(), AddPurchaseView, TimePickerDialog.OnTimeSetListener,
        FingerNavigatorInterface<AddPurchaseScreen> by FingerNavigator(com.bugtsa.casher.R.id.add_payment_container),
        BonePersisterInterface<AddPurchaseScreen>, AddPaymentStackPresentable {

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        managerProvider = ::getChildFragmentManager
    }

    override fun onDetach() {
        super.onDetach()
        managerProvider = null
    }

    override val fragmentTitle: String
        get() = getString(R.string.screen_add_purchase)

    override fun onRefresh() {
        super<FingerNavigatorInterface>.onRefresh()
        if (view == null) return

        val title = fragmentTitle
        toolbar.visibility = View.VISIBLE
        toolbar.title = title
        addNavigationToToolbar(toolbar, com.bugtsa.casher.R.drawable.ic_arrow_back_white)
    }

    //region ================= Add Purchase View =================

    override fun completedAddPurchase() {
        bone.dismiss()
    }

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
                categoriesList)
        category_purchase_et.setAdapter(adapter)
    }

    @SuppressLint("SetTextI18n")
    override fun setupCurrentDateAndTime(dateAndTime: String) {
        date_purchase.text = requireContext().getString(com.bugtsa.casher.R.string.current_date_and_time) + dateAndTime
    }

    @SuppressLint("SetTextI18n")
    override fun setupCustomDateAndTime(date: String, time: String) {
        date_purchase.text = requireContext().getString(com.bugtsa.casher.R.string.changed_date_and_time) + "$date $time"
    }

    //endregion

    //region ================= Calendar And Time Picker =================

    override fun showDatePicker() {
        val builder = Builder(activity, Builder.CalendarPickerOnConfirm { yearMonthDay ->
            viewModel.changeCalendar(yearMonthDay)
        })
        builder
                .setPromptText("Select Date")
                .setPromptSize(18)
                .setPromptColor(Color.RED)
        builder.show()
    }

    override fun showTimePicker() {
        val now = Calendar.getInstance()
        val tpd = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        )
        tpd.show(requireActivity().fragmentManager, "TagTimePickerDialog")
    }

    override fun onTimeSet(view: RadialPickerLayout?, hourOfDay: Int, minute: Int, hourOfDayEnd: Int, minuteEnd: Int) {
        val hourString = if (hourOfDay < 10) "0$hourOfDay" else "" + hourOfDay
        val minuteString = if (minute < 10) "0$minute" else "" + minute
        viewModel.changeTime(hourString, minuteString)
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
        viewModel.observeShowDatePicker().observe(viewLifecycleOwner,
                Observer {
                    showDatePicker()
                })
        viewModel.observeShowTimePicker().observe(viewLifecycleOwner,
                Observer {
                    showTimePicker()
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
            viewModel.checkCategorySaveOnDatabase(price_purchase_et.text.toString(),
                    category_purchase_et.text.toString())
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