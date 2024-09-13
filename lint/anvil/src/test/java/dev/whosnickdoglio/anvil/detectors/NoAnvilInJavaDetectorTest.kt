/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.anvil.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import dev.whosnickdoglio.lint.annotations.anvil.CONTRIBUTES_BINDING
import dev.whosnickdoglio.lint.annotations.anvil.CONTRIBUTES_MULTI_BINDING
import dev.whosnickdoglio.lint.annotations.anvil.CONTRIBUTES_SUBCOMPONENT
import dev.whosnickdoglio.lint.annotations.anvil.CONTRIBUTES_SUBCOMPONENT_FACTORY
import dev.whosnickdoglio.lint.annotations.anvil.CONTRIBUTES_TO
import dev.whosnickdoglio.lint.annotations.anvil.MERGE_COMPONENT
import dev.whosnickdoglio.lint.annotations.anvil.MERGE_SUBCOMPONENT
import dev.whosnickdoglio.stubs.anvilAnnotations
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class NoAnvilInJavaDetectorTest {
    @TestParameter(
        value =
            [
                CONTRIBUTES_TO,
                CONTRIBUTES_BINDING,
                CONTRIBUTES_MULTI_BINDING,
                CONTRIBUTES_SUBCOMPONENT,
                CONTRIBUTES_SUBCOMPONENT_FACTORY,
                MERGE_COMPONENT,
                MERGE_SUBCOMPONENT,
            ]
    )
    lateinit var annotation: String

    @Test
    fun `using Anvil annotation in java shows an error`() {
        TestLintTask.lint()
            .files(
                anvilAnnotations,
                TestFiles.java(
                        """
                import $annotation;

                @${annotation.substringAfterLast(".")}
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
                @${annotation.substringAfterLast(".")}
                ~${annotation.substringAfterLast(".").map { "~" }.joinToString(separator = "")}
                1 errors, 0 warnings
            """
                    .trimIndent()
            )
            .expectErrorCount(1)
    }

    @Test
    fun `using Anvil annotation in Kotlin shows no error`() {
        TestLintTask.lint()
            .files(
                anvilAnnotations,
                TestFiles.kotlin(
                        """
                import $annotation

                @${annotation.substringAfterLast(".")}
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
