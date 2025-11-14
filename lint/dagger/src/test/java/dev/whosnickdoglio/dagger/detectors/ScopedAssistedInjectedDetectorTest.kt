/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.dagger.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import dev.whosnickdoglio.stubs.daggerAnnotations
import dev.whosnickdoglio.stubs.daggerAssistedAnnotations
import dev.whosnickdoglio.stubs.javaxAnnotations
import org.junit.Test

class ScopedAssistedInjectedDetectorTest {

    private val myScope =
        TestFiles.kotlin(
            """
            import javax.inject.Scope
            @Scope annotation class MyScope
            """
                .trimIndent()
        )

    @Test
    fun `scoped kotlin class using @AssistedInject triggers error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                daggerAssistedAnnotations,
                javaxAnnotations,
                myScope,
                TestFiles.kotlin(
                        """
                import dagger.assisted.AssistedInject
                import dagger.assisted.Assisted

                @MyScope class MyAssistedClass @AssistedInject constructor(
                        private val myInt: Int,
                        @Assisted private val something: String
                )
                """
                    )
                    .indented(),
            )
            .issues(ScopedAssistedInjectedDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyAssistedClass.kt:4: Error: Classes using assisted inject cannot be scoped [ScopedAssistedInject]
                    @MyScope class MyAssistedClass @AssistedInject constructor(
                    ~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                    Fix for src/MyAssistedClass.kt line 4: Remove scope annotation:
                    @@ -4 +4
                    - @MyScope class MyAssistedClass @AssistedInject constructor(
                    +  class MyAssistedClass @AssistedInject constructor(
                """
                    .trimIndent()
            )
    }

    @Test
    fun `scoped java class using @AssistedInject triggers error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                daggerAssistedAnnotations,
                javaxAnnotations,
                myScope,
                TestFiles.java(
                        """
                import dagger.assisted.AssistedInject;
                import dagger.assisted.Assisted;

                @MyScope class MyAssistedClass {

                        @AssistedInject MyAssistedClass(
                            String something,
                            @Assisted Boolean somethingElse
                        ) {}
                }
                """
                    )
                    .indented(),
            )
            .issues(ScopedAssistedInjectedDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyAssistedClass.java:4: Error: Classes using assisted inject cannot be scoped [ScopedAssistedInject]
                    @MyScope class MyAssistedClass {
                    ~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                    Fix for src/MyAssistedClass.java line 4: Remove scope annotation:
                    @@ -4 +4
                    - @MyScope class MyAssistedClass {
                    +  class MyAssistedClass {
                """
                    .trimIndent()
            )
    }

    @Test
    fun `kotlin class without scope using @AssistedInject does not triggers error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                daggerAssistedAnnotations,
                javaxAnnotations,
                TestFiles.kotlin(
                        """
                import dagger.assisted.AssistedInject
                import dagger.assisted.Assisted

                class MyAssistedClass @AssistedInject constructor(
                        private val myInt: Int,
                        @Assisted private val something: String
                )
                """
                    )
                    .indented(),
            )
            .issues(ScopedAssistedInjectedDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `java class without scope using @AssistedInject does not triggers error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                daggerAssistedAnnotations,
                javaxAnnotations,
                TestFiles.java(
                        """
                import dagger.assisted.AssistedInject;
                import dagger.assisted.Assisted;

                class MyAssistedClass {

                        @AssistedInject MyAssistedClass(
                            String something,
                            @Assisted Boolean somethingElse
                        ) {}
                }
                """
                    )
                    .indented(),
            )
            .issues(ScopedAssistedInjectedDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `scoped kotlin class not using @AssistedInject does not triggers error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                daggerAssistedAnnotations,
                javaxAnnotations,
                myScope,
                TestFiles.kotlin(
                        """
                import javax.inject.Inject

                @MyScope class MyAssistedClass @Inject constructor(something: String)
                """
                    )
                    .indented(),
            )
            .issues(ScopedAssistedInjectedDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `scoped java class not using @AssistedInject does not triggers error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                daggerAssistedAnnotations,
                javaxAnnotations,
                myScope,
                TestFiles.java(
                        """
                import javax.inject.Inject;

                @MyScope class MyAssistedClass {

                        @Inject MyAssistedClass(
                            String something,
                            Boolean somethingElse
                        ) {}
                }
                """
                    )
                    .indented(),
            )
            .issues(ScopedAssistedInjectedDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }
}
