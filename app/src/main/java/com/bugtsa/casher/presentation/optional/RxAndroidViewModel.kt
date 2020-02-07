package com.bugtsa.casher.presentation.optional

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bugtsa.casher.global.ErrorHandler
import com.bugtsa.casher.global.contracts.ErrorObservableOwner
import com.bugtsa.casher.global.contracts.OnBackPressable
import com.hadilq.liveevent.LiveEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


open class RxAndroidViewModel(application: Application) : AndroidViewModel(application), OnBackPressable, ErrorObservableOwner {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val errorLiveData = LiveEvent<String>()
    protected val keyboardVisibilityEvent = LiveEvent<Boolean>()

    protected val progressStateLiveData = MutableLiveData<ProgressState>()
    fun getProgressStateLiveData(): LiveData<ProgressState> = progressStateLiveData

    fun addDispose(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    fun addDisposes(vararg disposables: Disposable) = disposables.forEach(::addDispose)

    fun unsubscribe() {
        compositeDisposable.clear()
    }

    override fun onCleared() {
        super.onCleared()
        unsubscribe()
    }

    override fun onBackPressed() {}

    override fun observeErrorLiveData() = errorLiveData

    fun observeKeyboardVisibilityEvent() = keyboardVisibilityEvent as LiveData<Boolean>

    protected open fun handleError(
            throwable: Throwable,
            showError: Boolean = true,
            defaultErrorMessage: String? = null
    ) {
        ErrorHandler.handle(throwable)
        if (showError) (throwable.message ?: defaultErrorMessage)?.let(errorLiveData::postValue)
    }
}