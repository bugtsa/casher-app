package com.bugtsa.casher.utils

import android.util.Log
import timber.log.Timber

@Suppress("unused")
class WhatATerribleFailure {
    fun <T> logAsWtf(clazz: Class<T>, message: String) {
        Timber.tag(clazz.name).wtf(message)

        wtf(message)
    }

    private fun wtf(message: String) {
        Timber.wtf(message)
        Log.wtf("dfsdf","dfsdf")
    }
}