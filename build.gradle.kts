// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        jcenter()
        maven { url = uri("https://maven.fabric.io/public") }
    }
    dependencies {
        val kotlinVersion = "1.3.72"

        classpath("com.android.tools.build:gradle:3.6.3")
        classpath("io.fabric.tools:gradle:1.31.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.google.gms:google-services:4.3.3")
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
        jcenter()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://oss.jfrog.org/artifactory/oss-snapshot-local") }
    }
}

//tasks.clear() {
//    clean (type: Delete) {
//        delete rootProject.buildDir
//    }
//}
