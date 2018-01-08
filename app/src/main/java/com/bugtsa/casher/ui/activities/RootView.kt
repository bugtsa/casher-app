package com.bugtsa.casher.ui.activities

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential

interface RootView {
    fun requestToApi(credential: GoogleAccountCredential)
}