package com.bugtsa.casher.ui.screens.settings

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.res.TypedArray
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bugtsa.casher.R
import com.bugtsa.casher.global.extentions.showAlertDialog
import com.bugtsa.casher.global.recycler.entities.*
import com.bugtsa.casher.presentation.SettingsViewModel
import com.bugtsa.casher.presentation.SettingsViewModelFactory
import com.bugtsa.casher.ui.screens.auth.SingUpScreen
import com.bugtsa.casher.ui.screens.base.BaseListFragment
import com.bugtsa.casher.utils.ThemeHelper
import kotlinx.android.synthetic.main.fragment_settings.*
import pro.horovodovodo4ka.bones.Finger
import pro.horovodovodo4ka.bones.Phalanx
import pro.horovodovodo4ka.bones.extensions.closest
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.ui.FragmentSibling
import pro.horovodovodo4ka.bones.ui.delegates.Page
import pro.horovodovodo4ka.bones.ui.extensions.addNavigationToToolbar
import pro.horovodovodo4ka.bones.ui.extensions.removeNavigationFromToolbar
import toothpick.Toothpick

interface NavigationStackPresentable {
    val fragmentTitle: String
        get() = "sdfsdfads"
}

open class SettingsScreen() : Phalanx() {
    override val seed = { SettingsScreenFragment() }
}

@SuppressLint("MissingSuperCall")
class SettingsScreenFragment(override val layout: Int = R.id.settings_container) : BaseListFragment(),
        BonePersisterInterface<SettingsScreen>,
        FragmentSibling<SettingsScreen> by Page() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = Toothpick
                .openScopes(activity, this)
                .getInstance(SettingsViewModelFactory::class.java)
        viewModel = ViewModelProvider(this, viewModelFactory)[SettingsViewModel::class.java]

        bindViewModel()

        onRefresh()
    }

    override fun onListItemClick(v: View, position: Int) {
        val item = adapter.getItems()[position]
        when (item.id) {
            R.id.profile_logout -> {
                requireContext().showAlertDialog(
                        getString(R.string.auth_want_to_logout_title),
                        message = getString(R.string.auth_want_to_logout_description),
                        positiveListener = DialogInterface.OnClickListener { dialog, _ ->
                            viewModel.logout()
                            dialog.dismiss()
                        }
                ).apply { show() }
            }
        }
    }

    override fun onItemsAddedToList() = Unit

    override fun onRefresh() {
        if (view == null) return

        val title = getString(R.string.settings_title)
        when (title) {
            null -> vToolbar.isVisible = false
            else -> {
                vToolbar.isVisible = true
                vToolbar.title = title

                if (bone.parents.size > 1) addNavigationToToolbar(vToolbar, R.drawable.ic_arrow_back_white)
                else removeNavigationFromToolbar(vToolbar)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super<BonePersisterInterface>.onSaveInstanceState(outState)
        super<BaseListFragment>.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<BonePersisterInterface>.onCreate(savedInstanceState)
        super<BaseListFragment>.onCreate(savedInstanceState)
    }

    private fun loadProfileStyle(): TypedArray = requireContext().obtainStyledAttributes(R.style.ProfileView, R.styleable.ProfileViewAttrs)

    private fun bindViewModel() {
        viewModel.observeLogout().observe(viewLifecycleOwner, Observer {
            val finger = bone.closest<Finger>() ?: return@Observer
            with(finger) {
                replace(rootPhalanx, SingUpScreen())
                popToRoot()
            }
        })

        viewModel.observeUserLogin().observe(viewLifecycleOwner, Observer { userLogin ->
            val items = mutableListOf<ListItem>().apply {

                val attributes = loadProfileStyle()
                val emptySpaceColor = attributes.getResourceId(R.styleable.ProfileViewAttrs_emptySpaceColor, 0)
                attributes.recycle()
                val settingsLeftMargin = 55
                val settingsRightMargin = 0

                add(SpaceItem(DEFAULT_SPACE, emptySpaceColor))
                add(DataItem(getString(R.string.profile_username), userLogin)).apply {
                    add(DividerItem(leftMargin = settingsLeftMargin, rightMargin = settingsRightMargin))
                }
                add(SwitchItem(
                        getString(R.string.switch_theme_mode),
                        CompoundButton.OnCheckedChangeListener { _, isChecked ->
                            bindCheckedListener(isChecked)
                        },
                        state = viewModel.observeModelTheme()
                )).apply {
                    add(DividerItem(leftMargin = settingsLeftMargin, rightMargin = settingsRightMargin))
                }
                add(MenuItem(
                        isArrowEnabled = false,
                        title = getString(R.string.profile_logout),
                        icon = R.drawable.ic_exit
                ).apply { id = R.id.profile_logout })
                add(DividerItem(leftMargin = settingsLeftMargin, rightMargin = settingsRightMargin))
            }
            adapter.setItems(items)
        })
    }

    private fun bindCheckedListener(isChecked: Boolean) {
        val currentTheme = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && isChecked -> ThemeHelper.darkMode
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !isChecked -> ThemeHelper.lightMode
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && isChecked -> ThemeHelper.batterySaverMode
            else -> ThemeHelper.default
        }
        viewModel.saveModeTheme(currentTheme)
    }

    companion object {
        private const val DEFAULT_SPACE = 16
    }

}