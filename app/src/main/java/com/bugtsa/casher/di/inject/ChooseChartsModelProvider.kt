package com.bugtsa.casher.di.inject

import com.bugtsa.casher.data.models.ChartsModel
import com.bugtsa.casher.networking.CasherApi
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ChooseChartsModelProvider(private val casherApi: CasherApi) : Provider<ChartsModel> {
    override fun get(): ChartsModel {
        return ChartsModel(casherApi)
    }
}