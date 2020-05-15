package com.bugtsa.casher

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles.java

object Stubs {

    /**
     * [TestFile] containing Log.java from the Android SDK.
     *
     * This is a hacky workaround for the Android SDK not being included on the Lint test harness
     * classpath. Ideally, we'd specify ANDROID_HOME as an environment variable.
     */
    val ANDROID_LOG_JAVA = java(
        """
                package android.util;
                
                public class Log {
                    public static void wtf(String tag, String msg) {
                        // Stub!
                    }
                }
            """.trimIndent())

    val LOG_WTF_KT = kotlin(
        "com/bugtsa/casher/app/WhatATerribleFailure.kt",
        """
                package ru.vtb.app
                
                import android.util.Log
                
                class WhatATerribleFailure {
                    fun <T> logAsWtf(clazz: Class<T>, message: String) {
                        Log.wtf(clazz.name, message)
                        wtf(message)
                    }
                
                    fun wtf(message: String) {
                        Log.d("TAG", message)
                    }
                }
            """).indented().within("src")

    val TIMBER_TAG_WFT_JAVA = java(
        """
            package timber.log;
            
            public class Timber {
                public static void wtf(@NonNls String message, Object... args) {
                    // Stub!
                }
            }
        """.trimIndent())

    val TIMBER_WFT_TAG_KT = kotlin(
        "com/bugtsa/casher/app/WhatATerribleFailure.kt",
        """
                package ru.vtb.app
                
                import timber.log
                
                class WhatATerribleFailure {
                    fun <T> logAsWtf(clazz: Class<T>, message: String) {
                        Timber.tag(clazz.name).wtf( message)
                        Timber.wtf( message)
                    }
                }
        """).indented().within("src")

    val LOG_WTF_JAVA = java(
        "com/bugtsa/casher/app/WhatATerribleFailureJava.java",
        """
                package ru.vtb;
                
                import android.util.Log;
                
                class WhatATerribleFailureJava {
                    void logAsWtf(Class<?> clazz, String message) {
                        Log.wtf(clazz.getName(), message);
                
                        wtf(message);
                    }
                
                    void wtf(String message) {
                        Log.d("TAG", message);
                    }
                }
            """).indented().within("src")
}
