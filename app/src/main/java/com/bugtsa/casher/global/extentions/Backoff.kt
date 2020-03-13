package com.bugtsa.casher.global.extentions

import com.bugtsa.casher.global.android.SystemTimeProvider
import java.util.Random
import kotlin.math.min

class Backoff(private val minDelay: Long = 100, private val maxDelay: Long = 20000, private val maxFailureCount: Int = 50) {

    private val random = Random(SystemTimeProvider.getCurrentTimeMillis())
    private var currentFailureCount = 0

    /**
     * Calculating wait duration after failure attempt
     *
     * @return wait in ms
     */
    @Synchronized
    fun exponentialWait(): Long {
        val maxDelayRet = if (maxFailureCount > MAX_FAILURE_COUNT_UNDEFINED) {
            (minDelay + (maxDelay - minDelay) / maxFailureCount * currentFailureCount)
        } else {
            minDelay * currentFailureCount
        }
        val spread = 0.8 + random.nextFloat() * 0.2
        return (spread * maxDelayRet).toLong()
    }

    /**
     * Notification about failure
     */
    @Synchronized
    fun onFailure() {
        currentFailureCount = if (maxFailureCount > MAX_FAILURE_COUNT_UNDEFINED) {
            min(++currentFailureCount, maxFailureCount)
        } else {
            ++currentFailureCount
        }
    }

    /**
     * Notification about success
     */
    @Synchronized
    fun onSuccess() {
        currentFailureCount = 0
    }

    companion object {
        const val MAX_FAILURE_COUNT_UNDEFINED = 0
    }
}