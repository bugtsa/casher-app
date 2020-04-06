package com.bugtsa.casher.ui.navigation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bugtsa.casher.R
import com.bugtsa.casher.ui.screens.auth.SingUpScreen
import kotlinx.android.synthetic.main.fragment_purchases_navigation.*
import kotlinx.android.synthetic.main.fragment_settings.*
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.Finger
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.ui.FingerNavigatorInterface
import pro.horovodovodo4ka.bones.ui.delegates.FingerNavigator
import pro.horovodovodo4ka.bones.ui.extensions.addNavigationToToolbar
import pro.horovodovodo4ka.bones.ui.extensions.removeNavigationFromToolbar

interface PurchasesStackPresentable {
    val fragmentTitle: String
}

open class PurchasesStack(rootPhalanx: Bone? = null) : Finger(rootPhalanx) {
    override val seed = { PurchasesNavigationFragment() }
}

@SuppressLint("MissingSuperCall")
open class PurchasesNavigationFragment : Fragment(),
    BonePersisterInterface<PurchasesStack>,
    FingerNavigatorInterface<PurchasesStack> by FingerNavigator(R.id.purchases_navigation_container){

    override fun onAttach(context: Context) {
        super.onAttach(context)
        managerProvider = ::getChildFragmentManager
    }

    override fun onDetach() {
        super.onDetach()
        managerProvider = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_purchases_navigation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bone.push(SingUpScreen())
    }

    override fun onRefresh() {
        super<FingerNavigatorInterface>.onRefresh()

        if (view == null) return

        val title = (bone.fingertip as? PurchasesStackPresentable)?.fragmentTitle
        when (title) {
            null -> toolbar.visibility = View.GONE
            else -> {
                toolbar.visibility = View.VISIBLE
                toolbar.title = title

                if (bone.phalanxes.size > 1) addNavigationToToolbar(toolbar, R.drawable.ic_arrow_back_white)
                else removeNavigationFromToolbar(toolbar)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super<BonePersisterInterface>.onSaveInstanceState(outState)
        super<androidx.fragment.app.Fragment>.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<BonePersisterInterface>.onCreate(savedInstanceState)
        super<androidx.fragment.app.Fragment>.onCreate(savedInstanceState)
    }

}