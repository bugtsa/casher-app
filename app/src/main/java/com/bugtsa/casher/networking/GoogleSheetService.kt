package com.bugtsa.casher.networking

import android.app.Application
import android.preference.PreferenceManager
import android.text.TextUtils
import com.bugtsa.casher.ui.activities.RootActivity
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.sheets.v4.SheetsScopes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSheetService @Inject constructor(application: Application){

//    var mCredential: GoogleAccountCredential
//    var mService : com.google.api.services.sheets.v4.Sheets

    companion object {
        private val SCOPES = mutableListOf(SheetsScopes.SPREADSHEETS)
    }

    init {
//        val token = GoogleAuthUtil.getToken(application.baseContext!!, "bugtsa@gmail.com", SheetsScopes.SPREADSHEETS)
//        mCredential = GoogleAccountCredential.usingOAuth2(
//                application.baseContext, SCOPES)
//                .setBackOff(ExponentialBackOff())
//        if (TextUtils.isEmpty(mCredential.selectedAccountName)) {
//            val settings = PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
//            val accountName = settings.getString(RootActivity.PREF_ACCOUNT_NAME, "")
//            mCredential.selectedAccountName = accountName
//        }
//
//        val transport = AndroidHttp.newCompatibleTransport()
//        val jsonFactory = JacksonFactory.getDefaultInstance()
//        mService = com.google.api.services.sheets.v4.Sheets.Builder(
//                transport, jsonFactory, mCredential)
//                .setApplicationName("Google Sheets API Android Quickstart")
//                .build()
    }
}