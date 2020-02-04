package com.bugtsa.casher.presentation

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bugtsa.casher.data.models.ChartsModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import toothpick.Toothpick
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChartsViewModelFactory @Inject constructor(private val app: Application) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            Toothpick.openScope(app).getInstance(modelClass) as T
}

class ChartsViewModel @Inject constructor(chartsModel: ChartsModel) : ViewModel() {

    private val rangeMonthLiveData = MutableLiveData<Pair<String, String>>()
    fun observeRangeMonth() = rangeMonthLiveData as LiveData<Pair<String, String>>

    init {
        chartsModel.getRangeMonths()
                .map { it.map { payment -> payment.date } }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ list ->
                    val first = list.first() ?: ""
                    val second = list.last() ?: ""
                    rangeMonthLiveData.value = first to second
                }, { th -> Timber.e(th) })
    }


}