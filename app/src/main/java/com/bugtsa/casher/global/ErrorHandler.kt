package com.bugtsa.todo.global

import org.koin.core.KoinComponent
import timber.log.Timber

object ErrorHandler : KoinComponent {

    fun handle(throwable: Throwable) {
        Timber.e(throwable)
    }

    fun handleNotImportant(throwable: Throwable) {
        Timber.i(throwable)
    }

    fun handleCallback(throwable: Throwable, callback: () -> Unit) {
        Timber.e(throwable)
        callback()
    }

}