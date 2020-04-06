package com.bugtsa.casher.ui.screens.settings

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bugtsa.casher.R
import com.bugtsa.casher.presentation.SettingsViewModel
import com.bugtsa.casher.presentation.SettingsViewModelFactory
import com.bugtsa.casher.utils.ThemeHelper
import kotlinx.android.synthetic.main.fragment_settings.*
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.Finger
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.ui.FingerNavigatorInterface
import pro.horovodovodo4ka.bones.ui.delegates.FingerNavigator
import pro.horovodovodo4ka.bones.ui.extensions.addNavigationToToolbar
import pro.horovodovodo4ka.bones.ui.extensions.removeNavigationFromToolbar
import toothpick.Toothpick

interface NavigationStackPresentable {
    val fragmentTitle: String
        get() = "sdfsdfads"
}

open class SettingsScreen(rootPhalanx: Bone? = null) : Finger(rootPhalanx) {
    override val seed = { SettingsScreenFragment() }
}

@SuppressLint("MissingSuperCall")
open class SettingsScreenFragment : Fragment(),
        BonePersisterInterface<SettingsScreen>,
        FingerNavigatorInterface<SettingsScreen> by FingerNavigator(R.id.settings_container) {

    private lateinit var viewModel: SettingsViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        managerProvider = ::getChildFragmentManager
    }

    override fun onDetach() {
        super.onDetach()
        managerProvider = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = Toothpick
                .openScopes(activity, this)
                .getInstance(SettingsViewModelFactory::class.java)
        viewModel = ViewModelProvider(this, viewModelFactory)[SettingsViewModel::class.java]

        bindListeners()
        bindViewModel()

        onRefresh()
    }

    private fun bindViewModel() {
        viewModel.observeModelTheme().observe(viewLifecycleOwner, Observer { isChecked ->
//            change_theme.isChecked = isChecked
        })

        viewModel.observeUserLogin().observe(viewLifecycleOwner, Observer { userLogin ->

        })
    }

    private fun bindListeners() {
//        change_theme.setOnCheckedChangeListener { _, isChecked ->
//            val currentTheme = when {
//                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && isChecked -> ThemeHelper.darkMode
//                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !isChecked -> ThemeHelper.lightMode
//                Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && isChecked -> ThemeHelper.batterySaverMode
//                else -> ThemeHelper.default
//            }
//            viewModel.saveModeTheme(currentTheme)
//        }
    }

    override fun onRefresh() {
        super<FingerNavigatorInterface>.onRefresh()

        if (view == null) return

        val title = getString(R.string.settings_title)
        when (title) {
            null -> vToolbar.visibility = View.GONE
            else -> {
                vToolbar.visibility = View.VISIBLE
                vToolbar.title = title

                if (bone.phalanxes.size > 1) addNavigationToToolbar(vToolbar, R.drawable.ic_arrow_back_white)
                else removeNavigationFromToolbar(vToolbar)
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

    // endregion


}