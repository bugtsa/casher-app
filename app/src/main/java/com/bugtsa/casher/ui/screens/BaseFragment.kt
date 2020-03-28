package com.bugtsa.casher.ui.screens

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bugtsa.casher.R
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

abstract class BaseFragment : Fragment() {

    abstract val layout: Int

    private var dialogProgress: AlertDialog? = null

    private var showCancelDialogProgress: Boolean = true
    private var cancelDialogProgress: AlertDialog? = null

    private var creationDialogDisposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(layout, null)

    protected open fun showProgress(
            cancelAction: (() -> Unit)? = null,
            cancelable: Boolean = true,
            delayToShow: Long = 0,
            isFullScreen: Boolean = false) {
        if (creationDialogDisposable?.isDisposed == false) return

        creationDialogDisposable = Completable.complete()
                .delay(delayToShow, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    context?.also { context ->
                        val dialogStyle = if (isFullScreen) {
                            R.style.FullScreenProgressDialog
                        } else {
                            R.style.ProgressDialog
                        }

                        dialogProgress = AlertDialog.Builder(context, dialogStyle)
                                .setOnCancelListener {
                                    showCancelProgressDialog(cancelAction)
                                }
                                .setCancelable(cancelable)
                                .create()

                        dialogProgress?.show()

                        dialogProgress?.window?.let { window ->
                            if (isFullScreen) {
                                initializeFullScreenDialog(window)
                            } else {
                                initializeSmallDialog(window)
                            }
                        }
                    }
                }
    }

    protected fun hideProgress() {
        creationDialogDisposable?.dispose()

        dialogProgress?.dismiss()
        dialogProgress = null

        cancelDialogProgress?.dismiss()
        cancelDialogProgress = null
    }

    protected fun processCancelDialogProgress(state: Boolean) {
        showCancelDialogProgress = state
        if (!showCancelDialogProgress) {
            cancelDialogProgress?.dismiss()
        }
    }

    protected open fun showCancelProgressDialog(cancelAction: (() -> Unit)? = null) {
        context?.also { context ->
            cancelDialogProgress = AlertDialog.Builder(context)
                    .setTitle(R.string.load_data_dialog_title)
                    .setMessage(R.string.load_data_dialog_content)
                    .setPositiveButton(R.string.load_data_dialog_positive_button) { dialog, _ ->
                        dialog.dismiss()
                        showProgress(cancelAction)
                    }
                    .setNegativeButton(R.string.load_data_dialog_negative_button) { dialog, _ ->
                        hideProgress()
                        dialog.dismiss()
                        cancelAction?.invoke()
                    }
                    .setOnCancelListener {
                        showProgress(cancelAction)
                    }
                    .create()
            if (showCancelDialogProgress) cancelDialogProgress?.show()
        }
    }

    open fun backPressAction() {
    }

//    fun addDisposable(disposable: Disposable) {
//        compositeDisposableDestroy.add(disposable)
//    }

    private fun initializeFullScreenDialog(window: Window) {
        window.setContentView(R.layout.view_progress_full_screen)
    }

    private fun initializeSmallDialog(window: Window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.setLayout(
                requireContext().resources.getDimensionPixelSize(R.dimen.base_dialog_progress_load_size),
                requireContext().resources.getDimensionPixelSize(R.dimen.base_dialog_progress_load_size))
        window.setContentView(R.layout.view_progress_small_dialog)
    }
}