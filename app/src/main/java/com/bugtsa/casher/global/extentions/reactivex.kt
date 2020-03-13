package com.bugtsa.casher.global.extentions

import com.bugtsa.casher.global.android.SystemTimeProvider
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.Function
import org.reactivestreams.Publisher
import java.util.concurrent.TimeUnit
import kotlin.math.min

/**
 * retry the single with exponential backoff
 *
 * @param maxDelay - the max delay in milliseconds
 * @param backoff - backoff with custom params
 * @param predicateStopExecuting - stop executing request if the error matches predicate function
 */
fun <T> Single<T>.exponentialRetry(
        maxDelay: Long = TimeUnit.MINUTES.toMillis(2),
        backoff: Backoff = Backoff(),
        predicateStopExecuting: ((Throwable) -> Boolean)? = null): Single<T> {
    return this.retryWhen(RetryWithBackoff(maxDelay, backoff, predicateStopExecuting))
}

/**
 * retry the flowable with exponential backoff
 *
 * @param maxDelay - the max delay in milliseconds
 * @param backoff - backoff with custom params
 * @param predicateStopExecuting - stop executing request if the error matches predicate function
 */
fun <T> Flowable<T>.exponentialRetry(
        maxDelay: Long = TimeUnit.MINUTES.toMillis(2),
        backoff: Backoff = Backoff(),
        predicateStopExecuting: ((Throwable) -> Boolean)? = null): Flowable<T> {
    return this.retryWhen(RetryWithBackoff(maxDelay, backoff, predicateStopExecuting))
}

private class RetryWithBackoff(
        maxDelay: Long,
        private val backoff: Backoff,
        private val predicateStopExecuting: ((Throwable) -> Boolean)?)
    : Function<Flowable<Throwable>, Publisher<*>> {

    private val endTime = SystemTimeProvider.getCurrentTimeMillis() + maxDelay

    override fun apply(throwableFlowable: Flowable<Throwable>): Publisher<*> {

        return throwableFlowable.flatMap { th ->

            backoff.onFailure()
            val cur = SystemTimeProvider.getCurrentTimeMillis()
            val isStopExecuting = predicateStopExecuting?.let { it(th) } == true

            if (cur < endTime && !isStopExecuting) {
                val delay = min(backoff.exponentialWait(), endTime - cur)
                Flowable.timer(delay, TimeUnit.MILLISECONDS)
            } else {
                Flowable.error<Any>(th)
            }
        }
    }
}
