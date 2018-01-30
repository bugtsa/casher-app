package com.bugtsa.casher.ui.screens.purchases.add_purchase

import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bugtsa.casher.R
import com.bugtsa.casher.databinding.ControllerAddPurchaseBinding
import com.bugtsa.casher.model.CategoryEntity
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject
import android.widget.ArrayAdapter


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

        addPurchaseScope = Toothpick.openScopes(activity, this)
        Toothpick.inject(this, addPurchaseScope)
        presenter.onAttachView(this)
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