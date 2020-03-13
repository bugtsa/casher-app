package com.bugtsa.casher.data.models.charts

import com.bugtsa.casher.global.extentions.Backoff
import com.bugtsa.casher.global.extentions.exponentialRetry
import com.bugtsa.casher.networking.CasherApi
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChooseChartsModel @Inject constructor(private val casherApi: CasherApi){

    init {
        instance = this
    }

    fun getRangeMonths() = casherApi.getRangeMonths()
            .exponentialRetry(RANGE_MONTH_TIMEOUT, Backoff(maxDelay = RANGE_MONTH_TIMEOUT))
            .timeout(RANGE_MONTH_TIMEOUT, TimeUnit.MILLISECONDS, Single.error(Throwable()))

    companion object {
        private var instance: ChooseChartsModel? = null

        private val RANGE_MONTH_TIMEOUT = TimeUnit.MINUTES.toMillis(1)
    }
}