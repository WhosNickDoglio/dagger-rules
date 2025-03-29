/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.dagger.detectors

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import dev.whosnickdoglio.stubs.daggerAnnotations
import dev.whosnickdoglio.stubs.javaxAnnotations
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class ScopedWithoutInjectAnnotationDetectorTest {
    @Test
    fun `kotlin scoped class without inject annotation triggers error message`(
        @TestParameter scopeFile: ScopeTestFile
    ) {
        TestLintTask.lint()
            .files(javaxAnnotations, scopeFile.file, TestFiles.kotlin("@MyScope class MyClass"))
            .issues(ScopedWithoutInjectAnnotationDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyClass.kt:1: Error: Without the @Inject annotation this class is not added to the DI graph which means the scope annotation doesn't do anything. [ScopedWithoutInjection]
                    @MyScope class MyClass
                    ~~~~~~~~
                    1 errors, 0 warnings
                """
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                    Fix for src/MyClass.kt line 1: Remove unnecessary scope annotation:
                    @@ -1 +1
                    - @MyScope class MyClass
                    +  class MyClass
                """
            )
    }

    @Test
    fun `java scoped class without inject annotation triggers error message`(
        @TestParameter scopeFile: ScopeTestFile
    ) {
        TestLintTask.lint()
            .files(javaxAnnotations, scopeFile.file, TestFiles.java("@MyScope class MyClass {}"))
            .issues(ScopedWithoutInjectAnnotationDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyClass.java:1: Error: Without the @Inject annotation this class is not added to the DI graph which means the scope annotation doesn't do anything. [ScopedWithoutInjection]
                    @MyScope class MyClass {}
                    ~~~~~~~~
                    1 errors, 0 warnings
                """
            )
            .expectFixDiffs(
                """
                    Fix for src/MyClass.java line 1: Remove unnecessary scope annotation:
                    @@ -1 +1
                    - @MyScope class MyClass {}
                    +  class MyClass {}
                """
            )
    }

    @Test
    fun `kotlin scoped class with inject annotation does not trigger error message`(
        @TestParameter scopeFile: ScopeTestFile
    ) {
        TestLintTask.lint()
            .files(
                javaxAnnotations,
                scopeFile.file,
                TestFiles.kotlin(
                    """
                import javax.inject.Inject

                @MyScope class MyClass @Inject constructor()
                    """
                ),
            )
            .issues(ScopedWithoutInjectAnnotationDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `java scoped class with inject annotation does not trigger error message`(
        @TestParameter scopeFile: ScopeTestFile
    ) {
        TestLintTask.lint()
            .files(
                javaxAnnotations,
                scopeFile.file,
                TestFiles.java(
                    """
                    import javax.inject.Inject;

                    @MyScope class MyClass {

                        @Inject public MyClass() {}
                    }
                """
                ),
            )
            .issues(ScopedWithoutInjectAnnotationDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `kotlin class without scope but with inject annotation does not trigger error message`() {
        TestLintTask.lint()
            .files(
                javaxAnnotations,
                TestFiles.kotlin(
                    """
                    import javax.inject.Inject

                    class MyClass @Inject constructor()
                """
                ),
            )
            .issues(ScopedWithoutInjectAnnotationDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `java class without scope with inject annotation does not trigger error message`() {
        TestLintTask.lint()
            .files(
                javaxAnnotations,
                TestFiles.java(
                    """
                    import javax.inject.Inject;

                    class MyClass {

                        @Inject public MyClass() {}
                    }
                """
                ),
            )
            .issues(ScopedWithoutInjectAnnotationDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `kotlin class with no scope or inject annotations shows no error message`() {
        TestLintTask.lint()
            .files(TestFiles.kotlin("class MyClass"))
            .issues(ScopedWithoutInjectAnnotationDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `java class with no scope or inject annotations shows no error message`() {
        TestLintTask.lint()
            .files(TestFiles.java("class MyClass {}"))
            .issues(ScopedWithoutInjectAnnotationDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `kotlin @Component definition with a scope does not show an error message`(
        @TestParameter scopeFile: ScopeTestFile
    ) {
        TestLintTask.lint()
            .files(
                javaxAnnotations,
                scopeFile.file,
                daggerAnnotations,
                TestFiles.kotlin(
                    """
                import dagger.Component

                @MyScope @Component interface MyComponent
            """
                ),
            )
            .issues(ScopedWithoutInjectAnnotationDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `java @Component definition with a scope does not show an error message`(
        @TestParameter scopeFile: ScopeTestFile
    ) {
        TestLintTask.lint()
            .files(
                javaxAnnotations,
                scopeFile.file,
                daggerAnnotations,
                TestFiles.java(
                    """
                import dagger.Component;

                @MyScope @Component interface MyComponent {}
            """
                ),
            )
            .issues(ScopedWithoutInjectAnnotationDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }
}

@Suppress("unused")
enum class ScopeTestFile(val file: TestFile) {
    KOTLIN(
        TestFiles.kotlin(
            """
        import javax.inject.Scope
        @Scope annotation class MyScope
        """
        )
    ),
    JAVA(
        TestFiles.java(
            """
        import javax.inject.Scope;

        @Scope
        public @interface MyScope {}
        """
        )
    ),
}
