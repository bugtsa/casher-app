package com.bugtsa.todo.global.contracts

import androidx.lifecycle.LiveData

interface ErrorObservableOwner {

    fun observeErrorLiveData(): LiveData<String>
}
