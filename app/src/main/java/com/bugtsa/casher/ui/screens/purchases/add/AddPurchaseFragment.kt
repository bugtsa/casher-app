package com.bugtsa.casher.ui.screens.purchases.add

import android.annotation.SuppressLint
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
import pro.horovodovodo4ka.bones.Finger
import pro.horovodovodo4ka.bones.Phalanx
import pro.horovodovodo4ka.bones.extensions.closest
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.ui.ScreenInterface
import pro.horovodovodo4ka.bones.ui.delegates.Page
import toothpick.Scope
import toothpick.Toothpick
import java.util.*
import javax.inject.Inject


class AddPurchaseScreen : Phalanx() {

    fun showRootView() {
        val bot = closest<Finger>()?.phalanxes?.first()
    }

    data class ArgbValues(val alpha: Int,
                          val red: Int,
                          val green: Int,
                          val blue: Int) {

        override fun toString(): String {
            return "Alpha: ${this.alpha} Red: ${this.red} Green: ${this.green} Blue: ${this.blue}"
        }
    }

    lateinit var argbValues: ArgbValues
    val color = Random().let {
        argbValues = ArgbValues(alpha = 255, red = it.nextInt(256),
                green = it.nextInt(256), blue = it.nextInt(256))
        Color.argb(argbValues.alpha, argbValues.red, argbValues.green, argbValues.blue)
    }

    override val seed = { AddPurchaseFragment() }
}

@Suppress("DEPRECATED_IDENTITY_EQUALS")
@SuppressLint("MissingSuperCall")
class AddPurchaseFragment : Fragment(), AddPurchaseView, TimePickerDialog.OnTimeSetListener,
        ScreenInterface<AddPurchaseScreen> by Page(), BonePersisterInterface<AddPurchaseScreen> {

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
        cancel_purchase.setOnClickListener { processBackPress() }

        view.setBackgroundColor(bone.color)
        color_demo.text = bone.argbValues.toString()

        refreshUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onViewDestroy()
        Toothpick.closeScope(this)
    }

    //endregion

    override fun onSaveInstanceState(outState: Bundle) {
        super<BonePersisterInterface>.onSaveInstanceState(outState)
        super<androidx.fragment.app.Fragment>.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<BonePersisterInterface>.onCreate(savedInstanceState)
        super<androidx.fragment.app.Fragment>.onCreate(savedInstanceState)
    }

    //region ================= Add Purchase View =================

    override fun completedAddPurchase() {
        processBackPress()
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