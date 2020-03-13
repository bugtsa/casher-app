package com.bugtsa.casher.global.android

import android.os.SystemClock

object SystemTimeProvider {
    fun getElapsedRealtime(): Long {
        return SystemClock.elapsedRealtime()
    }

    fun getCurrentTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}