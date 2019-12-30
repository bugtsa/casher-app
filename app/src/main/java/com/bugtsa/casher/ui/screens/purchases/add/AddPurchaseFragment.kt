package com.bugtsa.casher.ui.screens.purchases.add

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.borax12.materialdaterangepicker.time.RadialPickerLayout
import com.borax12.materialdaterangepicker.time.TimePickerDialog
import com.bugtsa.casher.R
import com.maxproj.calendarpicker.Builder
import kotlinx.android.synthetic.main.controller_add_purchase.*
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

open class AddPurchaseScreen(rootPhalanx: Bone? = null) : Finger(rootPhalanx), AddPaymentStackPresentable {
    data class ArgbValues(val alpha: Int,
                          val red: Int,
                          val green: Int,
                          val blue: Int) {

        override fun toString(): String {
            return "R: ${this.red} G: ${this.green} B: ${this.blue}"
        }
    }

    var argbValues: ArgbValues
    val color = Random().let {
        argbValues = ArgbValues(alpha = 255, red = it.nextInt(256),
                green = it.nextInt(256), blue = it.nextInt(256))
        Color.argb(argbValues.alpha, argbValues.red, argbValues.green, argbValues.blue)
    }

    override val seed = { AddPurchaseFragment() }

    override val fragmentTitle: String
        get() = argbValues.toString()
}

@Suppress("DEPRECATED_IDENTITY_EQUALS")
@SuppressLint("MissingSuperCall")
class AddPurchaseFragment : Fragment(R.layout.controller_add_purchase), AddPurchaseView, TimePickerDialog.OnTimeSetListener,
        FingerNavigatorInterface<AddPurchaseScreen> by FingerNavigator(com.bugtsa.casher.R.id.add_payment_container),
        BonePersisterInterface<AddPurchaseScreen> {

    private lateinit var viewModel: AddPurchaseViewModel

    //region ================= Implements Methods =================

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = Toothpick
                .openScopes(activity, this)
                .getInstance(AddPurchaseViewModelFactory::class.java)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[AddPurchaseViewModel::class.java]

        bindListeners()
        bindViewModel()

        view.setBackgroundColor(bone.color)
        color_demo.text = bone.argbValues.toString()

        onRefresh()
    }

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
                Observer {
                    showProgressBar()
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

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onViewDestroy()
        Toothpick.closeScope(this)
    }

    //endregion

    override fun onSaveInstanceState(outState: Bundle) {
        super<BonePersisterInterface>.onSaveInstanceState(outState)
        super<Fragment>.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<BonePersisterInterface>.onCreate(savedInstanceState)
        super<Fragment>.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        managerProvider = ::getChildFragmentManager
    }

    override fun onDetach() {
        super.onDetach()
        managerProvider = null
    }

    override fun onRefresh() {
        super<FingerNavigatorInterface>.onRefresh()

        if (view == null) return

        val title = bone.fragmentTitle
        toolbar.visibility = View.VISIBLE
        toolbar.title = title
        toolbar.setOnClickListener {
            val cm = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val cData = ClipData.newPlainText("text", title)
            cm.primaryClip = cData
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        addNavigationToToolbar(toolbar, com.bugtsa.casher.R.drawable.ic_arrow_back_white)
    }

    //region ================= Add Purchase View =================

    override fun completedAddPurchase() {
        bone.dismiss()
    }

    override fun showProgressBar() {
        TODO()
    }

    override fun hideProgressBar() {
        TODO()
    }

    override fun setSearchText(result: String) {
        TODO()
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
//        date_purchase.text = requireContext().getString(com.bugtsa.casher.R.string.changed_date_and_time) + "$date $time"
    }

    //endregion

    //region ================= Calendar And Time Picker =================

    override fun showDatePicker() {
        var builder = Builder(activity, Builder.CalendarPickerOnConfirm { yearMonthDay ->
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