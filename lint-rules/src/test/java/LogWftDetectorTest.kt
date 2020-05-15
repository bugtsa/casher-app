package com.bugtsa.casher

import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.bugtsa.casher.Stubs.ANDROID_LOG_JAVA
import com.bugtsa.casher.Stubs.LOG_WTF_KT
import com.bugtsa.casher.Stubs.TIMBER_TAG_WFT_JAVA
import com.bugtsa.casher.Stubs.TIMBER_WFT_TAG_KT
import org.junit.*

class LogWftDetectorTest {

    @Test
    fun testLogWtfDetector() {
        /* ktlint-disable max-line-length */
        val expected = """
src/com/bugtsa/casher/app/WhatATerribleFailure.kt:7: Error: Usage of Log.wtf() is prohibited [LogWtfUsageError]
        Log.wtf(clazz.name, message)
            ~~~
1 errors, 0 warnings
            """.trimIndent()
        /* ktlint-enable max-line-length */

        lint().files(
            ANDROID_LOG_JAVA,
            LOG_WTF_KT)
            .allowMissingSdk() // The one SDK class that we need has been added manually!
            .issues(LogWtfDetector.ISSUE)
            .run()
            .expect(expected.trimIndent())
    }

    @Test
    fun testTimberWtfDetector() {
        /* ktlint-disable max-line-length */
        val expected = """
src/com/bugtsa/casher/app/WhatATerribleFailure.kt:7: Error: Usage of Timber.wtf() is prohibited [LogWtfUsageError]
        Timber.wtf(clazz.name, message)
            ~~~
1 errors, 0 warnings
            """.trimIndent()
        /* ktlint-enable max-line-length */

        lint().files(
            TIMBER_TAG_WFT_JAVA,
            TIMBER_WFT_TAG_KT)
            .allowMissingSdk() // The one SDK class that we need has been added manually!
            .issues(LogWtfDetector.ISSUE)
            .run()
            .expect(expected.trimIndent())
    }
}