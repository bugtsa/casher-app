package com.bugtsa.casher.ui.screens.purchases.add

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
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
import toothpick.Scope
import toothpick.Toothpick
import java.util.*
import javax.inject.Inject

interface AddPaymentStackPresentable {
    val fragmentTitle: String
}


//class AddPurchaseScreen : Phalanx() {
open class AddPurchaseScreen (rootPhalanx: Bone? = null) : Finger(rootPhalanx), AddPaymentStackPresentable {
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
        get() = "[${(parentBone as? Finger)?.phalanxes?.size ?: 0}] " + argbValues.toString()
}

@Suppress("DEPRECATED_IDENTITY_EQUALS")
@SuppressLint("MissingSuperCall")
class AddPurchaseFragment : Fragment(), AddPurchaseView, TimePickerDialog.OnTimeSetListener,
//        ScreenInterface<AddPurchaseScreen> by Page(),
        FingerNavigatorInterface<AddPurchaseScreen> by FingerNavigator(R.id.add_payment_container),
        BonePersisterInterface<AddPurchaseScreen> {

    @Inject
    lateinit var presenter: AddPurchasePresenter

    private lateinit var addPurchaseScope: Scope

    //region ================= Implements Methods =================

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.controller_add_purchase, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoriesTouchListener()
        add_date_purchase.setOnClickListener {
            presenter.checkSetupCustomDateAndTime(add_date_purchase.isChecked)
        }

        addPurchaseScope = Toothpick.openScopes(activity, this)
        Toothpick.inject(this, addPurchaseScope)
        presenter.onAttachView(this)
        presenter.setupCurrentDate()
        presenter.checkExistCategoriesInDatabase()

        save_purchase.setOnClickListener {
            presenter.checkCategorySaveOnDatabase(price_purchase_et.text.toString(),
                    category_purchase_et.text.toString())
        }
        cancel_purchase.setOnClickListener { completedAddPurchase() }

        view.setBackgroundColor(bone.color)
        color_demo.text = bone.argbValues.toString()

        onRefresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onViewDestroy()
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

    override fun onAttach(context: Context?) {
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
        when (title) {
            null -> toolbar.visibility = View.GONE
            else -> {
                toolbar.visibility = View.VISIBLE
                toolbar.title = title

                addNavigationToToolbar(toolbar, R.drawable.ic_arrow_back_white)
            }
        }
    }

    //region ================= Add Purchase View =================

    override fun completedAddPurchase() {
//        bone.processBackPress()
//        bone.goBack()
        bone.dismiss()
    }

    override fun showProgressBar() {
    }

    override fun hideProgressBar() {
    }

    override fun setSearchText(result: String) {
    }

    override fun setupCategoriesList(categoriesList: List<String>) {
        val adapter: ArrayAdapter<String> = ArrayAdapter(
                activity,
                android.R.layout.simple_dropdown_item_1line,
                categoriesList)
        category_purchase_et.setAdapter(adapter)
    }

    override fun setupCurrentDateAndTime(dateAndTime: String) {
        date_purchase.text = activity!!.resources.getString(R.string.current_date_and_time) + dateAndTime
    }

    override fun setupCustomDateAndTime(date: String, time: String) {
        date_purchase.text = activity!!.resources.getString(R.string.changed_date_and_time) + "$date $time"
    }

    //endregion

    //region ================= Calendar And Time Picker =================

    override fun showDatePicker() {
        var builder = Builder(activity, Builder.CalendarPickerOnConfirm { yearMonthDay ->
            presenter.changeCalendar(yearMonthDay)
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
        tpd.show(activity!!.fragmentManager, "TagTimePickerDialog")
    }

    override fun onTimeSet(view: RadialPickerLayout?, hourOfDay: Int, minute: Int, hourOfDayEnd: Int, minuteEnd: Int) {
        val hourString = if (hourOfDay < 10) "0$hourOfDay" else "" + hourOfDay
        val minuteString = if (minute < 10) "0$minute" else "" + minute
        presenter.changeTime(hourString, minuteString)
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