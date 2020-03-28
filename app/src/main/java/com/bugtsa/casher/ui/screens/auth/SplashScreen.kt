package com.bugtsa.casher.ui.screens.auth

import android.annotation.SuppressLint
import android.os.Bundle
import com.bugtsa.casher.R
import com.bugtsa.casher.ui.screens.BaseFragment
import com.bugtsa.casher.ui.screens.settings.NavigationStackPresentable
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.BoneSibling
import pro.horovodovodo4ka.bones.Phalanx
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.ui.FragmentSibling
import pro.horovodovodo4ka.bones.ui.delegates.Page


class SplashScreen : Phalanx(), NavigationStackPresentable {
    override val seed: () -> BoneSibling<out Bone>
        get() = { SplashFragment() }

    override val fragmentTitle: String
        get() = ""
}

@SuppressLint("MissingSuperCall")
class SplashFragment(override val layout: Int = R.layout.fragment_splash) : BaseFragment(),
        BonePersisterInterface<SplashScreen>,
        FragmentSibling<SplashScreen> by Page() {

    override fun onSaveInstanceState(outState: Bundle) {
        super<BonePersisterInterface>.onSaveInstanceState(outState)
        super<BaseFragment>.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<BonePersisterInterface>.onCreate(savedInstanceState)
        super<BaseFragment>.onCreate(savedInstanceState)
    }

}