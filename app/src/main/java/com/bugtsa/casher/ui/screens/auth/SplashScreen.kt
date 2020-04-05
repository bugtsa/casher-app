package com.bugtsa.casher.ui.screens.auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bugtsa.casher.R
import com.bugtsa.casher.presentation.SplashViewModel
import com.bugtsa.casher.presentation.SplashViewModelFactory
import com.bugtsa.casher.ui.navigation.TabBar
import com.bugtsa.casher.ui.screens.BaseFragment
import com.bugtsa.casher.ui.screens.charts.ChooseChartsScreen
import com.bugtsa.casher.ui.screens.purchases.show.PurchasesScreen
import com.bugtsa.casher.ui.screens.settings.NavigationStackPresentable
import com.bugtsa.casher.ui.screens.settings.SettingsScreen
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.BoneSibling
import pro.horovodovodo4ka.bones.Finger
import pro.horovodovodo4ka.bones.Phalanx
import pro.horovodovodo4ka.bones.extensions.closest
import pro.horovodovodo4ka.bones.extensions.show
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.ui.FragmentSibling
import pro.horovodovodo4ka.bones.ui.delegates.Page
import toothpick.Toothpick


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

    lateinit var viewModel: SplashViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.checkAuthenticatedUser()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val splashViewModelFactory = Toothpick.openScopes(activity, this)
                .getInstance(SplashViewModelFactory::class.java)
        viewModel = ViewModelProvider(this, splashViewModelFactory)[SplashViewModel::class.java]

        bindViewModel()
    }

    private fun bindViewModel() {
        viewModel.observeIsAuthenticatedUser().observe(viewLifecycleOwner, Observer { isAuth ->
            if (isAuth) {
                val finger = bone.closest<Finger>() ?: return@Observer
                switchToMainScreen(finger)
            } else {
                bone.show(SingUpScreen())
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super<BonePersisterInterface>.onSaveInstanceState(outState)
        super<BaseFragment>.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<BonePersisterInterface>.onCreate(savedInstanceState)
        super<BaseFragment>.onCreate(savedInstanceState)
    }

    companion object {
        fun switchToMainScreen(finger: Finger) {
            val mainScreen = TabBar(
                    PurchasesScreen(),
                    ChooseChartsScreen(),
                    SettingsScreen()
            )

            with(finger) {
                replace(rootPhalanx, mainScreen)
                popToRoot()
            }
        }
    }

}