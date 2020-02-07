package com.bugtsa.casher.global.contracts

import androidx.lifecycle.LiveData

interface ErrorObservableOwner {

    fun observeErrorLiveData(): LiveData<String>
}
