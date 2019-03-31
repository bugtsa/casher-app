package com.bugtsa.casher.ui.activities

import android.os.Bundle
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential

interface RootView {
    fun requestToApi(credential: GoogleAccountCredential, savedInstanceState: Bundle?)
}