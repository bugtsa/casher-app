plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("io.gitlab.arturbosch.detekt")
}

detekt {
    config = files("detekt-config.yml")
}

android {
    compileSdkVersion(29)

    defaultConfig {
        applicationId = "com.bugtsa.casher"
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0$version"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                    getDefaultProguardFile("proguard-android.txt"),
                    "proguard-rules.pro"
            )
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

    val kotlinVersion = System.getProperty("kotlinVersion")
    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
    implementation(kotlin)

    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("com.google.android.material:material:1.1.0")
    implementation("androidx.legacy:legacy-support-v13:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation(Libs.Android.coreX)
    implementation(Libs.Android.coreKtx)

    implementation("com.google.firebase:firebase-analytics:17.4.0")
    implementation("com.google.android.gms:play-services-auth:18.0.0")
    implementation("pub.devrel:easypermissions:2.0.0")
    implementation("com.google.api-client:google-api-client-android:1.25.0") {
        exclude(mapOf("group" to "org.apache.httpcomponents"))
    }

    implementation(Libs.Arch.lifeCycleExtensions)
    implementation(Libs.Arch.viewModelKtx)
    implementation("com.github.hadilq.liveevent:liveevent:1.2.0")

    implementation(Libs.Auth.apiClient)
    implementation(Libs.Auth.androidApiClient)

    implementation("com.google.apis:google-api-services-people:v1-rev4-1.22.0")

    implementation("com.github.stephanenicolas.toothpick:toothpick-runtime:1.1.1")
    kapt("com.github.stephanenicolas.toothpick:toothpick-compiler:1.1.1")
    testImplementation("com.github.stephanenicolas.toothpick:toothpick-testing:1.1.1")

    implementation("io.reactivex.rxjava2:rxjava:2.2.19")
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

    implementation("androidx.room:room-runtime:2.2.5")
    implementation("androidx.room:room-rxjava2:2.2.5")
    kapt("androidx.room:room-compiler:2.2.5")

    // Timber
    implementation("com.jakewharton.timber:timber:4.7.1")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.11.0")
    kapt("com.github.bumptech.glide:compiler:4.11.0")
    implementation("jp.wasabeef:glide-transformations:4.1.0")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.11.0") {
        mapOf("transitive" to false)
    }

    // Charts
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.github.AnyChart:AnyChart-Android:1.1.2")

    // Calendar & Date Picker
    implementation("com.github.maxyou:CalendarPicker:v1.1.2")
    implementation("com.borax12.materialdaterangepicker:library:1.9")

    implementation("androidx.multidex:multidex:2.0.1")

    testImplementation ("junit:junit:4.12")
    testImplementation ("com.google.truth:truth:0.42")

    lintChecks(project(":lint-rules"))
}

kapt {
    generateStubs = true

    arguments {
        arg("toothpick_registry_package_name", "com.bugtsa.casher")
    }
}
