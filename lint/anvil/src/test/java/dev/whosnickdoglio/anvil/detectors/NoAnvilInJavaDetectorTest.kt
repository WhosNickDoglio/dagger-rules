/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.anvil.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.google.testing.junit.testparameterinjector.TestParameterValuesProvider
import dev.whosnickdoglio.stubs.anvilAnnotations
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class NoAnvilInJavaDetectorTest {

    private class NoAnvilInJavaTestParameterValuesProvider : TestParameterValuesProvider() {
        override fun provideValues(context: Context?): List<*> =
            NoAnvilInJavaDetector.anvilAnnotations.toList()
    }

    @TestParameter(valuesProvider = NoAnvilInJavaTestParameterValuesProvider::class)
    lateinit var annotation: String

    @Test
    fun `using Anvil annotation in java shows an error`() {
        val scopeString = "(AppScope.class)"
        TestLintTask.lint()
            .files(
                anvilAnnotations,
                TestFiles.java(
                        """
                import $annotation;

                @${annotation.substringAfterLast(".")}(AppScope.class)
                class MyJavaClass {}
            """
                    )
                    .indented(),
            )
            .issues(NoAnvilInJavaDetector.ISSUE)
            .run()
            .expect(
                """
                src/MyJavaClass.java:3: Error: Anvil works as a Kotlin compiler plugin and does not support being used from Java. You can convert this class to Kotlin so it can use Anvil annotations.

                See https://whosnickdoglio.dev/dagger-rules/rules/#anvil-cannot-be-used-from-java for more information. [NoAnvilJavaUsage]
                @${annotation.substringAfterLast(".")}$scopeString
                ~${annotation.substringAfterLast(".").map { "~" }.joinToString(separator = "")}${scopeString.map { "~" }.joinToString(separator = "")}
                1 errors, 0 warnings
            """
                    .trimIndent()
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                Autofix for src/MyJavaClass.java line 3: Remove ${annotation.substringAfterLast(".")}:
                @@ -3 +3
                - @${annotation.substringAfterLast(".")}$scopeString
            """
                    .trimIndent()
            )
    }

    @Test
    fun `using Anvil annotation in Kotlin shows no error`() {
        TestLintTask.lint()
            .files(
                anvilAnnotations,
                TestFiles.kotlin(
                        """
                import $annotation

                @${annotation.substringAfterLast(".")}(AppScope::class)
                class MyKotlinClass
            """
                    )
                    .indented(),
            )
            .issues(NoAnvilInJavaDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }
}
