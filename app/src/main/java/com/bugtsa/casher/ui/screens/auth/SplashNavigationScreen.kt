package com.bugtsa.casher.ui.screens.auth

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bugtsa.casher.R
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.Finger
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.ui.FingerNavigatorInterface
import pro.horovodovodo4ka.bones.ui.delegates.FingerNavigator

class SplashNavigationScreen(rootPhalanx: Bone? = null) : Finger(rootPhalanx) {

    override val seed = { SplashNavigationFragment() }
}

@SuppressLint("MissingSuperCall")
class SplashNavigationFragment : Fragment(R.layout.fragment_splash_navigation),
        FingerNavigatorInterface<SplashNavigationScreen> by FingerNavigator(R.id.splash_navigation_container),
        BonePersisterInterface<SplashNavigationScreen> {

    override fun onSaveInstanceState(outState: Bundle) {
        super<BonePersisterInterface>.onSaveInstanceState(outState)
        super<Fragment>.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<BonePersisterInterface>.onCreate(savedInstanceState)
        super<Fragment>.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshUI()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        managerProvider = ::getChildFragmentManager
    }

    override fun onDestroy() {
        super.onDestroy()
        managerProvider = null
    }

}