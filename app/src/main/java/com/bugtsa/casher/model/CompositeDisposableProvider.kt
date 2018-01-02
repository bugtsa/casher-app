package com.bugtsa.casher.model

import io.reactivex.disposables.CompositeDisposable
import javax.inject.Provider

class CompositeDisposableProvider : Provider<CompositeDisposable> {
    override fun get(): CompositeDisposable {
        return CompositeDisposable()
    }
}
