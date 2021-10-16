plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.appdistribution")
    id("io.gitlab.arturbosch.detekt")
}

detekt {
    config = files("detekt-config.yml")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        applicationId = "com.bugtsa.casher"
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 1
        versionName = Libs.Project.fullVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            firebaseCrashlytics {
                mappingFileUploadEnabled = false
            }
        }

        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                    getDefaultProguardFile("proguard-android.txt"),
                    "proguard-rules.pro"
            )
            firebaseAppDistribution {
                appId = "1:495380633023:android:a1b3fc39656f235e1abd0a"
                serviceCredentialsFile = "$rootDir/app/google-services.json"
                testers = "bugtsa@gmail.com, preispodhyaya@gmail.com"
            }
        }
    }

    flavorDimensions("default")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    configurations {
        all {
            exclude(mapOf("module" to "httpclient"))
            exclude(mapOf("module" to "commons-logging"))
        }
    }

    packagingOptions {
        exclude("META-INF/rxjava.properties")
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/license.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/notice.txt")
        exclude("META-INF/ASL2.0")
    }
}

//buildChecks {
//    androidSdk {
//        it.compileSdkVersion = 29
//        revision = 4
//    }
//    javaVersion {
//        version = JavaVersion.VERSION_1_8
//    }
//    enableByDefault = false
//}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    val kotlinVersion = "1.4.31"

    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
    implementation(kotlin)

    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.legacy:legacy-support-v13:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation(Libs.Android.coreX)
    implementation(Libs.Android.coreKtx)

    implementation("com.google.firebase:firebase-analytics:19.0.0")
    implementation("com.google.firebase:firebase-crashlytics:18.0.0")
    implementation("com.google.android.gms:play-services-auth:19.0.0")
    implementation("pub.devrel:easypermissions:3.0.0")
    implementation("com.google.api-client:google-api-client-android:1.30.10") {
        exclude(mapOf("group" to "org.apache.httpcomponents"))
    }

    implementation(Libs.Arch.lifeCycleExtensions)
    implementation(Libs.Arch.viewModelKtx)
    implementation("com.github.hadilq.liveevent:liveevent:1.2.0")

    implementation(Libs.Auth.apiClient)
    implementation(Libs.Auth.androidApiClient)

    implementation("com.google.apis:google-api-services-people:v1-rev99-1.22.0")

    implementation("com.github.stephanenicolas.toothpick:toothpick-runtime:3.1.0")
    implementation("com.github.stephanenicolas.toothpick:smoothie-androidx:3.1.0")
    kapt("com.github.stephanenicolas.toothpick:toothpick-compiler:3.1.0")

    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")

    implementation(Libs.Network.retrofit)
    implementation(Libs.Network.okhttp)
    implementation(Libs.Network.gson)
    implementation(Libs.Network.moshi)
    implementation(Libs.Network.adapterRxJava2)

    // AdapterDelegates
    implementation("com.hannesdorfmann:adapterdelegates4:4.3.0")

    //Bones
    implementation("com.github.horovodovodo4ka:bones:1.2.12")

    implementation("com.jakewharton.rxbinding2:rxbinding:2.2.0")

    // Room Database

    implementation("androidx.room:room-runtime:2.3.0")
    implementation("androidx.room:room-rxjava2:2.3.0")
    kapt("androidx.room:room-compiler:2.3.0")

    // Timber
    implementation("com.jakewharton.timber:timber:4.7.1")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")
    implementation("jp.wasabeef:glide-transformations:4.3.0")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.12.0") {
        mapOf("transitive" to false)
    }

    // Charts
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.github.AnyChart:AnyChart-Android:1.1.2")

    // Calendar & Date Picker
    implementation("com.github.maxyou:CalendarPicker:v1.1.2")
    implementation("com.borax12.materialdaterangepicker:library:2.0")

    implementation("androidx.multidex:multidex:2.0.1")

    testImplementation ("junit:junit:4.13.2")
    testImplementation ("com.google.truth:truth:1.0.1")

    lintChecks(project(":lint-rules"))
}

kapt {
    generateStubs = true

    arguments {
        arg("toothpick_registry_package_name", "com.bugtsa.casher")
    }
}
