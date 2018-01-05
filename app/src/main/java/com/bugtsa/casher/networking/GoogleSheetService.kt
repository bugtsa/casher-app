package com.bugtsa.casher.networking

import android.app.Application
import android.content.Context
import com.bugtsa.casher.CasherApp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.sheets.v4.SheetsScopes
import toothpick.Toothpick
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSheetService @Inject constructor(application: Application){

    var mCredential: GoogleAccountCredential

    companion object {
        private val SCOPES = mutableListOf(SheetsScopes.DRIVE)
    }

    init {
        mCredential = GoogleAccountCredential.usingOAuth2(
                application.baseContext, SCOPES)
                .setBackOff(ExponentialBackOff())
    }
}