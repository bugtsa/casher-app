package com.bugtsa.casher.global.rx

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers as RxSchedulers

object SchedulersProvider : SchedulersContract {

    override fun ui() = AndroidSchedulers.mainThread()
    override fun trampoline() = RxSchedulers.trampoline()
    override fun io() = RxSchedulers.io()
}

interface SchedulersContract {

    fun ui(): Scheduler
    fun trampoline(): Scheduler
    fun io(): Scheduler
}