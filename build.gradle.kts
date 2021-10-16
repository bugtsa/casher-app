// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
    }
    dependencies {
        val kotlinVersion = System.getProperty("kotlinVersion")

        classpath("com.android.tools.build:gradle:7.0.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.google.gms:google-services:4.3.4")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.5.2")
        classpath("com.google.firebase:firebase-appdistribution-gradle:2.1.1")
    }
}

plugins {
    id("com.github.ben-manes.versions")
    id("io.gitlab.arturbosch.detekt")
    id("org.sonarqube")
//    id("com.avito.android.buildchecks")
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

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://oss.jfrog.org/artifactory/oss-snapshot-local") }
    }
}
