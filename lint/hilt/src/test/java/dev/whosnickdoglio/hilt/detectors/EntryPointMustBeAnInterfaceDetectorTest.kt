/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.hilt.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import dev.whosnickdoglio.lint.shared.hilt.ENTRY_POINT
import org.junit.Test

class EntryPointMustBeAnInterfaceDetectorTest {
    @Test
    fun `kotlin @EntryPoint usage on an interface does not show an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                    """
                    import $ENTRY_POINT

                    @EntryPoint
                    interface MyEntryPoint
                """
                        .trimIndent(),
                ),
            )
            .issues(EntryPointMustBeAnInterfaceDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `java @EntryPoint usage on an interface does not show an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.java(
                    """
                    import $ENTRY_POINT;

                    @EntryPoint
                    interface MyEntryPoint {}
                """
                        .trimIndent(),
                ),
            )
            .issues(EntryPointMustBeAnInterfaceDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin @EntryPoint usage on an abstract class shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                    """
                    import $ENTRY_POINT

                    @EntryPoint
                    abstract class MyEntryPoint
                """
                        .trimIndent(),
                ),
            )
            .issues(EntryPointMustBeAnInterfaceDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyEntryPoint.kt:3: Error: The @EntryPoint annotation can only be applied to interfaces, trying to apply it to anything else will cause an error at compile time.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#the-entrypoint-annotation-can-only-be-applied-to-interfaces for more information. [EntryPointMustBeAnInterface]
                    @EntryPoint
                    ^
                    1 errors, 0 warnings
                """
                    .trimIndent(),
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
                    .trimIndent(),
            )
    }

    @Test
    fun `java @EntryPoint usage on an abstract class shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.java(
                    """
                    import $ENTRY_POINT;

                    @EntryPoint
                    abstract class MyEntryPoint {}
                """
                        .trimIndent(),
                ),
            )
            .issues(EntryPointMustBeAnInterfaceDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyEntryPoint.java:3: Error: The @EntryPoint annotation can only be applied to interfaces, trying to apply it to anything else will cause an error at compile time.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#the-entrypoint-annotation-can-only-be-applied-to-interfaces for more information. [EntryPointMustBeAnInterface]
                    @EntryPoint
                    ^
                    1 errors, 0 warnings
                """
                    .trimIndent(),
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
                    .trimIndent(),
            )
    }

    @Test
    fun `kotlin @EntryPoint usage on an concrete class shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                    """
                    import $ENTRY_POINT

                    @EntryPoint
                    class MyEntryPoint
                """
                        .trimIndent(),
                ),
            )
            .issues(EntryPointMustBeAnInterfaceDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyEntryPoint.kt:3: Error: The @EntryPoint annotation can only be applied to interfaces, trying to apply it to anything else will cause an error at compile time.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#the-entrypoint-annotation-can-only-be-applied-to-interfaces for more information. [EntryPointMustBeAnInterface]
                    @EntryPoint
                    ^
                    1 errors, 0 warnings
                """
                    .trimIndent(),
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
                    .trimIndent(),
            )
    }

    @Test
    fun `java @EntryPoint usage on an concrete class shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.java(
                    """
                    import $ENTRY_POINT;

                    @EntryPoint
                    class MyEntryPoint {}
                """
                        .trimIndent(),
                ),
            )
            .issues(EntryPointMustBeAnInterfaceDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyEntryPoint.java:3: Error: The @EntryPoint annotation can only be applied to interfaces, trying to apply it to anything else will cause an error at compile time.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#the-entrypoint-annotation-can-only-be-applied-to-interfaces for more information. [EntryPointMustBeAnInterface]
                    @EntryPoint
                    ^
                    1 errors, 0 warnings
                """
                    .trimIndent(),
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
                    .trimIndent(),
            )
    }

    @Test
    fun `@EntryPoint usage on a kotlin object shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                    """
                    import $ENTRY_POINT

                    @EntryPoint
                    object MyEntryPoint
                """
                        .trimIndent(),
                ),
            )
            .issues(EntryPointMustBeAnInterfaceDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyEntryPoint.kt:3: Error: The @EntryPoint annotation can only be applied to interfaces, trying to apply it to anything else will cause an error at compile time.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#the-entrypoint-annotation-can-only-be-applied-to-interfaces for more information. [EntryPointMustBeAnInterface]
                    @EntryPoint
                    ^
                    1 errors, 0 warnings
                """
                    .trimIndent(),
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
                    .trimIndent(),
            )
    }

    @Test
    fun `@EntryPoint usage on kotlin enum shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                    """
                    import $ENTRY_POINT

                    @EntryPoint
                    enum class MyEntryPoint
                """
                        .trimIndent(),
                ),
            )
            .issues(EntryPointMustBeAnInterfaceDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyEntryPoint.kt:3: Error: The @EntryPoint annotation can only be applied to interfaces, trying to apply it to anything else will cause an error at compile time.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#the-entrypoint-annotation-can-only-be-applied-to-interfaces for more information. [EntryPointMustBeAnInterface]
                    @EntryPoint
                    ^
                    1 errors, 0 warnings
                """
                    .trimIndent(),
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
                    .trimIndent(),
            )
    }

    @Test
    fun `@EntryPoint usage on a java enum class shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.java(
                    """
                    import $ENTRY_POINT;

                    @EntryPoint
                    enum MyEntryPoint {}
                """
                        .trimIndent(),
                ),
            )
            .issues(EntryPointMustBeAnInterfaceDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyEntryPoint.java:3: Error: The @EntryPoint annotation can only be applied to interfaces, trying to apply it to anything else will cause an error at compile time.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#the-entrypoint-annotation-can-only-be-applied-to-interfaces for more information. [EntryPointMustBeAnInterface]
                    @EntryPoint
                    ^
                    1 errors, 0 warnings
                """
                    .trimIndent(),
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
                    .trimIndent(),
            )
    }

    @Test
    fun `@EntryPoint usage on kotlin data shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                    """
                    import $ENTRY_POINT

                    @EntryPoint
                    data class MyEntryPoint
                """
                        .trimIndent(),
                ),
            )
            .issues(EntryPointMustBeAnInterfaceDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyEntryPoint.kt:3: Error: The @EntryPoint annotation can only be applied to interfaces, trying to apply it to anything else will cause an error at compile time.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#the-entrypoint-annotation-can-only-be-applied-to-interfaces for more information. [EntryPointMustBeAnInterface]
                    @EntryPoint
                    ^
                    1 errors, 0 warnings
                """
                    .trimIndent(),
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
                    .trimIndent(),
            )
    }

    @Test
    fun `@EntryPoint usage on kotlin sealed shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                    """
                    import $ENTRY_POINT

                    @EntryPoint
                    sealed class MyEntryPoint
                """
                        .trimIndent(),
                ),
            )
            .issues(EntryPointMustBeAnInterfaceDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyEntryPoint.kt:3: Error: The @EntryPoint annotation can only be applied to interfaces, trying to apply it to anything else will cause an error at compile time.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#the-entrypoint-annotation-can-only-be-applied-to-interfaces for more information. [EntryPointMustBeAnInterface]
                    @EntryPoint
                    ^
                    1 errors, 0 warnings
                """
                    .trimIndent(),
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
                    .trimIndent(),
            )
    }

    @Test
    fun `@EntryPoint usage on kotlin annotation shows an error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                    """
                    import $ENTRY_POINT

                    @EntryPoint
                    annotation class MyEntryPoint
                """
                        .trimIndent(),
                ),
            )
            .issues(EntryPointMustBeAnInterfaceDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyEntryPoint.kt:3: Error: The @EntryPoint annotation can only be applied to interfaces, trying to apply it to anything else will cause an error at compile time.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#the-entrypoint-annotation-can-only-be-applied-to-interfaces for more information. [EntryPointMustBeAnInterface]
                    @EntryPoint
                    ^
                    1 errors, 0 warnings
                """
                    .trimIndent(),
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
                    .trimIndent(),
            )
    }
}
