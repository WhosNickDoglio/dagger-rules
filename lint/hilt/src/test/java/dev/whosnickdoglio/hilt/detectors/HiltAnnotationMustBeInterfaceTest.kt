/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.hilt.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.google.testing.junit.testparameterinjector.TestParameterValuesProvider
import dev.whosnickdoglio.stubs.hiltAnnotations
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class HiltAnnotationMustBeInterfaceTest {

    private class MustBeInterfaceTestParameterValuesProvider : TestParameterValuesProvider() {
        override fun provideValues(context: Context?): List<*> =
            HiltAnnotationMustBeInterface.annotations.toList()
    }

    @TestParameter(valuesProvider = MustBeInterfaceTestParameterValuesProvider::class)
    lateinit var annotation: String

    @Test
    fun `kotlin hilt annotation usage on an interface does not show an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                    """
                    import $annotation

                    @${annotation.substringAfterLast(".")}
                    interface MyEntryPoint
                """
                        .trimIndent()
                ),
            )
            .issues(HiltAnnotationMustBeInterface.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `java hilt annotation usage on an interface does not show an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.java(
                    """
                    import $annotation;

                    @${annotation.substringAfterLast(".")}
                    interface MyEntryPoint {}
                """
                        .trimIndent()
                ),
            )
            .issues(HiltAnnotationMustBeInterface.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin hilt annotation usage on an abstract class shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                    """
                    import $annotation

                    @${annotation.substringAfterLast(".")}
                    abstract class MyEntryPoint
                """
                        .trimIndent()
                ),
            )
            .issues(HiltAnnotationMustBeInterface.ISSUE)
            .run()
            .expect(
                """
                    src/MyEntryPoint.kt:3: Error: The @EntryPoint and DefineComponent annotations can only be applied to interfaces, trying to apply it to anything else will cause an error at compile time.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#the-entrypoint-annotation-can-only-be-applied-to-interfaces for more information. [HiltMustBeAnInterface]
                    @${annotation.substringAfterLast(".")}
                    ^
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                    Fix for src/MyEntryPoint.kt line 3: Make MyEntryPoint an interface:
                    @@ -4 +4
                    - abstract class MyEntryPoint
                    @@ -5 +4
                    + interface MyEntryPoint
                """
                    .trimIndent()
            )
    }

    @Test
    fun `java hilt annotation usage on an abstract class shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.java(
                    """
                    import $annotation;

                    @${annotation.substringAfterLast(".")}
                    abstract class MyEntryPoint {}
                """
                        .trimIndent()
                ),
            )
            .issues(HiltAnnotationMustBeInterface.ISSUE)
            .run()
            .expect(
                """
                    src/MyEntryPoint.java:3: Error: The @EntryPoint and DefineComponent annotations can only be applied to interfaces, trying to apply it to anything else will cause an error at compile time.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#the-entrypoint-annotation-can-only-be-applied-to-interfaces for more information. [HiltMustBeAnInterface]
                    @${annotation.substringAfterLast(".")}
                    ^
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                Fix for src/MyEntryPoint.java line 3: Make MyEntryPoint an interface:
                @@ -4 +4
                - abstract class MyEntryPoint {}
                @@ -5 +4
                + interface MyEntryPoint {}
            """
                    .trimIndent()
            )
    }

    @Test
    fun `kotlin hilt annotation usage on an concrete class shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                    """
                    import $annotation

                    @${annotation.substringAfterLast(".")}
                    class MyEntryPoint
                """
                        .trimIndent()
                ),
            )
            .issues(HiltAnnotationMustBeInterface.ISSUE)
            .run()
            .expect(
                """
                    src/MyEntryPoint.kt:3: Error: The @EntryPoint and DefineComponent annotations can only be applied to interfaces, trying to apply it to anything else will cause an error at compile time.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#the-entrypoint-annotation-can-only-be-applied-to-interfaces for more information. [HiltMustBeAnInterface]
                    @${annotation.substringAfterLast(".")}
                    ^
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                Fix for src/MyEntryPoint.kt line 3: Make MyEntryPoint an interface:
                @@ -4 +4
                - class MyEntryPoint
                @@ -5 +4
                + interface MyEntryPoint
            """
                    .trimIndent()
            )
    }

    @Test
    fun `java hilt annotation usage on an concrete class shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.java(
                    """
                    import $annotation;

                    @${annotation.substringAfterLast(".")}
                    class MyEntryPoint {}
                """
                        .trimIndent()
                ),
            )
            .issues(HiltAnnotationMustBeInterface.ISSUE)
            .run()
            .expect(
                """
                    src/MyEntryPoint.java:3: Error: The @EntryPoint and DefineComponent annotations can only be applied to interfaces, trying to apply it to anything else will cause an error at compile time.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#the-entrypoint-annotation-can-only-be-applied-to-interfaces for more information. [HiltMustBeAnInterface]
                    @${annotation.substringAfterLast(".")}
                    ^
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                Fix for src/MyEntryPoint.java line 3: Make MyEntryPoint an interface:
                @@ -4 +4
                - class MyEntryPoint {}
                @@ -5 +4
                + interface MyEntryPoint {}
            """
                    .trimIndent()
            )
    }

    @Test
    fun `hilt annotation usage on a kotlin object shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                    """
                    import $annotation

                    @${annotation.substringAfterLast(".")}
                    object MyEntryPoint
                """
                        .trimIndent()
                ),
            )
            .issues(HiltAnnotationMustBeInterface.ISSUE)
            .run()
            .expect(
                """
                    src/MyEntryPoint.kt:3: Error: The @EntryPoint and DefineComponent annotations can only be applied to interfaces, trying to apply it to anything else will cause an error at compile time.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#the-entrypoint-annotation-can-only-be-applied-to-interfaces for more information. [HiltMustBeAnInterface]
                    @${annotation.substringAfterLast(".")}
                    ^
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                Fix for src/MyEntryPoint.kt line 3: Make MyEntryPoint an interface:
                @@ -4 +4
                - object MyEntryPoint
                @@ -5 +4
                + interface MyEntryPoint
            """
                    .trimIndent()
            )
    }

    @Test
    fun `hilt annotation usage on kotlin enum shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                    """
                    import $annotation

                    @${annotation.substringAfterLast(".")}
                    enum class MyEntryPoint
                """
                        .trimIndent()
                ),
            )
            .issues(HiltAnnotationMustBeInterface.ISSUE)
            .run()
            .expect(
                """
                    src/MyEntryPoint.kt:3: Error: The @EntryPoint and DefineComponent annotations can only be applied to interfaces, trying to apply it to anything else will cause an error at compile time.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#the-entrypoint-annotation-can-only-be-applied-to-interfaces for more information. [HiltMustBeAnInterface]
                    @${annotation.substringAfterLast(".")}
                    ^
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                Fix for src/MyEntryPoint.kt line 3: Make MyEntryPoint an interface:
                @@ -4 +4
                - enum class MyEntryPoint
                @@ -5 +4
                + interface MyEntryPoint
            """
                    .trimIndent()
            )
    }

    @Test
    fun `hilt annotation usage on a java enum class shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.java(
                    """
                    import $annotation;

                    @${annotation.substringAfterLast(".")}
                    enum MyEntryPoint {}
                """
                        .trimIndent()
                ),
            )
            .issues(HiltAnnotationMustBeInterface.ISSUE)
            .run()
            .expect(
                """
                    src/MyEntryPoint.java:3: Error: The @EntryPoint and DefineComponent annotations can only be applied to interfaces, trying to apply it to anything else will cause an error at compile time.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#the-entrypoint-annotation-can-only-be-applied-to-interfaces for more information. [HiltMustBeAnInterface]
                    @${annotation.substringAfterLast(".")}
                    ^
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                    Fix for src/MyEntryPoint.java line 3: Make MyEntryPoint an interface:
                    @@ -4 +4
                    - enum MyEntryPoint {}
                    @@ -5 +4
                    + interface MyEntryPoint {}
                """
                    .trimIndent()
            )
    }

    @Test
    fun `hilt annotation usage on kotlin data shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                    """
                    import $annotation

                    @${annotation.substringAfterLast(".")}
                    data class MyEntryPoint
                """
                        .trimIndent()
                ),
            )
            .issues(HiltAnnotationMustBeInterface.ISSUE)
            .run()
            .expect(
                """
                    src/MyEntryPoint.kt:3: Error: The @EntryPoint and DefineComponent annotations can only be applied to interfaces, trying to apply it to anything else will cause an error at compile time.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#the-entrypoint-annotation-can-only-be-applied-to-interfaces for more information. [HiltMustBeAnInterface]
                    @${annotation.substringAfterLast(".")}
                    ^
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                Fix for src/MyEntryPoint.kt line 3: Make MyEntryPoint an interface:
                @@ -4 +4
                - data class MyEntryPoint
                @@ -5 +4
                + interface MyEntryPoint
            """
                    .trimIndent()
            )
    }

    @Test
    fun `hilt annotation usage on kotlin sealed shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                    """
                    import $annotation

                    @${annotation.substringAfterLast(".")}
                    sealed class MyEntryPoint
                """
                        .trimIndent()
                ),
            )
            .issues(HiltAnnotationMustBeInterface.ISSUE)
            .run()
            .expect(
                """
                    src/MyEntryPoint.kt:3: Error: The @EntryPoint and DefineComponent annotations can only be applied to interfaces, trying to apply it to anything else will cause an error at compile time.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#the-entrypoint-annotation-can-only-be-applied-to-interfaces for more information. [HiltMustBeAnInterface]
                    @${annotation.substringAfterLast(".")}
                    ^
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                Fix for src/MyEntryPoint.kt line 3: Make MyEntryPoint an interface:
                @@ -4 +4
                - sealed class MyEntryPoint
                @@ -5 +4
                + interface MyEntryPoint
            """
                    .trimIndent()
            )
    }

    @Test
    fun `hilt annotation usage on kotlin annotation shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                    """
                    import $annotation

                    @${annotation.substringAfterLast(".")}
                    annotation class MyEntryPoint
                """
                        .trimIndent()
                ),
            )
            .issues(HiltAnnotationMustBeInterface.ISSUE)
            .run()
            .expect(
                """
                    src/MyEntryPoint.kt:3: Error: The @EntryPoint and DefineComponent annotations can only be applied to interfaces, trying to apply it to anything else will cause an error at compile time.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#the-entrypoint-annotation-can-only-be-applied-to-interfaces for more information. [HiltMustBeAnInterface]
                    @${annotation.substringAfterLast(".")}
                    ^
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                Fix for src/MyEntryPoint.kt line 3: Make MyEntryPoint an interface:
                @@ -4 +4
                - annotation class MyEntryPoint
                @@ -5 +4
                + interface MyEntryPoint
            """
                    .trimIndent()
            )
    }
}
