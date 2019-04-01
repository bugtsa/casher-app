package com.bugtsa.casher.di.module

import android.app.*
import com.bugtsa.casher.arch.models.*
import com.bugtsa.casher.di.inject.*
import com.bugtsa.casher.domain.local.database.*
import com.bugtsa.casher.domain.local.preference.*
import com.bugtsa.casher.networking.*
import io.reactivex.disposables.*
import toothpick.config.*

class CasherApplicationModule : Module {

	constructor(application: Application) {
		bind(CompositeDisposable::class.java).toProviderInstance(CompositeDisposableProvider())
		bind(CasherApi::class.java).toProviderInstance(CasherRestApiProvider())

		bind(Application::class.java).toProviderInstance(ApplicationProvider(application))
		bind(LocalSettingsRepository::class.java).toProviderInstance(PreferenceProvider(application))
		bind(PurchaseModel::class.java).toProviderInstance(PurchaseModelProvider())

		val categoryDao = CategoryDaoProvider(application)
		bind(CategoryDao::class.java).toProviderInstance(categoryDao)
		bind(LocalCategoryDataStore::class.java).toProviderInstance(
			LocalCategoryDateStoreProvider(
				categoryDao.get()
			)
		)
	}
}