package com.bugtsa.casher.ui.screens.purchases.add_purchase

import android.databinding.DataBindingUtil
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bugtsa.casher.R
import com.bugtsa.casher.databinding.ControllerAddPurchaseBinding
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject
import android.widget.ArrayAdapter
import com.maxproj.calendarpicker.Builder


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class AddPurchaseController : Controller(), AddPurchaseView {

    lateinit var binding: ControllerAddPurchaseBinding

    @Inject
    lateinit var presenter: AddPurchasePresenter

    lateinit private var addPurchaseScope: Scope

    //region ================= Implements Methods =================

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        var view: View = inflater.inflate(R.layout.controller_add_purchase, container, false)
        binding = DataBindingUtil.bind(view)

        setupCategoriesTouchListener()
        binding.addDatePurchase.setOnClickListener({ viewAddDatePurchase ->
            presenter.checkShowDateAndTimePickers(binding.addDatePurchase.isChecked)
        })

        addPurchaseScope = Toothpick.openScopes(activity, this)
        Toothpick.inject(this, addPurchaseScope)
        presenter.onAttachView(this)
        presenter.setupCurrentDate()
        presenter.checkExistCategoriesInDatabase()

        return view
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        presenter.onViewDestroy()
        Toothpick.closeScope(this)
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

    override fun setSearchText(result: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setupCategoriesList(categoriesList: List<String>) {
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                activity,
                android.R.layout.simple_dropdown_item_1line,
                categoriesList)
        binding.categoryPurchaseEt.setAdapter(adapter)
    }

    override fun setupCurrentDate(dateAndTimeString: String) {
        binding.datePurchase.text = activity!!.resources.getString(R.string.current_date_and_time) + dateAndTimeString
    }

    override fun setupChangedDate(date: String, time: String) {
        binding.datePurchase.text = activity!!.resources.getString(R.string.changed_date_and_time) + date + " " + time
    }

    //region ================= Calendar And Time Picker =================

    override fun setupCalendarPicker() {
        var builder = Builder(activity, Builder.CalendarPickerOnConfirm { yearMonthDay ->
            presenter.changeCalendar(yearMonthDay)
        })
        builder
                .setPromptText("Select Date")
                .setPromptSize(18)
                .setPromptColor(Color.RED)
//                .setPromptBgColor(0xFFFFFFFF)

        builder.show()

//        Builder.CalendarPickerOnConfirm.onComplete(CalendarPickerOnConfirm)
    }

    //endregion

    //endregion


    //region ================= Categories List Methods =================

    private fun setupCategoriesTouchListener() {
        binding.categoryPurchaseEt.setOnTouchListener({ v, event ->
            if (event.getAction() === MotionEvent.ACTION_UP) {
                if (event.getRawX() >= binding.categoryPurchaseEt.getRight() - binding.categoryPurchaseEt.getTotalPaddingRight()) {
                    resetSearchView()
                }
            }
            false
        })
    }

    private fun resetSearchView() {
        binding.categoryPurchaseEt.text.clear()
    }

    //endregion


    private fun popCurrentController() {
        router.popCurrentController()
    }
}