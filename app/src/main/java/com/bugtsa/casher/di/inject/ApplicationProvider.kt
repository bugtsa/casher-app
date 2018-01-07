package com.bugtsa.casher.di.inject

import android.app.Application
import javax.inject.Provider

class ApplicationProvider : Provider<Application> {

    var application : Application

    constructor(application: Application) {
        this.application = application
    }

    override fun get(): Application {
        return this.application
    }
}