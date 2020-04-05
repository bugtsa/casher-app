package com.bugtsa.casher.presentation

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bugtsa.casher.domain.prefs.PreferenceRepository
import com.bugtsa.casher.presentation.optional.RxViewModel
import toothpick.Toothpick
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SplashViewModelFactory @Inject constructor(private val app: Application) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return Toothpick.openScope(app).getInstance(modelClass) as T
    }
}

class SplashViewModel @Inject constructor(private val preferenceRepository: PreferenceRepository) : RxViewModel() {

    private val isAuthenticatedUserLiveData = MutableLiveData<Boolean>()
    fun observeIsAuthenticatedUser(): LiveData<Boolean> = isAuthenticatedUserLiveData

    fun checkAuthenticatedUser() {
        isAuthenticatedUserLiveData.value = preferenceRepository.getAccessToken().isNotEmpty() && preferenceRepository.getRefreshToken().isNotEmpty()
    }
}