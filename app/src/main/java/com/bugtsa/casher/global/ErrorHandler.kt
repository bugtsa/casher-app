package com.bugtsa.casher.global

import timber.log.Timber

object ErrorHandler {

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