object Libs {

    object Project {
        const val kotlinVersion = "1.3.72"
        const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
    }

    object Android {
        const val androidArchVersion = "1.1.1"
        private const val androidxCore = "1.3.0-rc01"
        const val coreX = "androidx.core:core:$androidxCore"
        const val coreKtx = "androidx.core:core-ktx:$androidxCore"
    }

    object Arch {
        private const val lifecycle = "2.2.0"
        const val lifeCycleExtensions = "androidx.lifecycle:lifecycle-extensions:$lifecycle"
        const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle"
    }

    object Auth {
        private const val apiAuth = "1.25.0"
        const val apiClient = "com.google.api-client:google-api-client:$apiAuth"
        const val androidApiClient = "com.google.api-client:google-api-client-android:$apiAuth"
    }

    object Network {
        private const val retrofitVersion = "2.6.0"
        const val retrofit = "com.squareup.retrofit2:retrofit:$retrofitVersion"
        const val okhttp = "com.squareup.okhttp:okhttp:$retrofitVersion"
        const val gson = "com.squareup.retrofit2:converter-gson:$retrofitVersion"
        const val moshi = "com.squareup.retrofit2:converter-moshi:$retrofitVersion"
        const val adapterRxJava2 = "com.squareup.retrofit2:adapter-rxjava2:$retrofitVersion"
    }
//    rxJavaVersion = "2.1.0"
//    rxAndroidVersion = "2.0.1"
//    priorityJobQueueVersion = "2.0.1"
//    playServicesVersion = "11.4.2"
//    retrofitVersion = "2.1.0"
//    okHttpVersion = "3.4.1"
//    rxRelayVersion = "2.0.0"
//    rxLintVersion = "1.6"
//    rxkotlin = "2.4.0"
//    version = "2"
}