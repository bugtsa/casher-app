package com.bugtsa.casher.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations

inline fun <X : Any?, Y : Any?> (LiveData<X>).switchMap(crossinline mapFunction: (X) -> LiveData<Y>): LiveData<Y> {
    return Transformations.switchMap(this) {
        mapFunction(it)
    }
}

inline fun <X : Any?, Y : Any?> (LiveData<X>).map(crossinline mapFunction: (X) -> Y): LiveData<Y> {
    return Transformations.map(this) {
        mapFunction(it)
    }
}

fun <X, Y> zipLiveData(a: LiveData<X>, b: LiveData<Y>): LiveData<Pair<X, Y>> {
    return MediatorLiveData<Pair<X, Y>>().apply {
        var lastX: X? = null
        var lastY: Y? = null

        fun update() {
            val localX = lastX
            val localY = lastY
            if (localX != null && localY != null) {
                this.postValue(Pair(localX, localY))
            }
        }

        addSource(a) {
            lastX = it
            update()
        }
        addSource(b) {
            lastY = it
            update()
        }
    }
}

fun <X, Y, Z> zipLiveData(a: LiveData<X>, b: LiveData<Y>, c: LiveData<Z>): LiveData<Triple<X, Y, Z>> {
    return MediatorLiveData<Triple<X, Y, Z>>().apply {
        var lastX: X? = null
        var lastY: Y? = null
        var lastZ: Z? = null

        fun update() {
            val localX = lastX
            val localY = lastY
            val localZ = lastZ
            if (localX != null && localY != null && localZ != null) {
                this.postValue(Triple(localX, localY, localZ))
            }
        }

        addSource(a) {
            lastX = it
            update()
        }

        addSource(b) {
            lastY = it
            update()
        }

        addSource(c) {
            lastZ = it
            update()
        }
    }
}
