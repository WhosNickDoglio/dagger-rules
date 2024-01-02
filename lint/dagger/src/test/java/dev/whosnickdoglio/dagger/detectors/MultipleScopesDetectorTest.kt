/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.dagger.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import dev.whosnickdoglio.stubs.daggerAnnotations
import dev.whosnickdoglio.stubs.javaxAnnotations
import org.junit.Test

class MultipleScopesDetectorTest {

    private val scopes =
        TestFiles.kotlin(
            """
            import javax.inject.Scope
            @Scope annotation class MyScope
            @Scope annotation class MyOtherScope
            """
                .trimIndent()
        )

    @Test
    fun `kotlin class with multiple scopes triggers an error`() {
        TestLintTask.lint()
            .files(
                scopes,
                javaxAnnotations,
                TestFiles.kotlin(
                    """
                    import javax.inject.Inject

                    @MyScope
                    @MyOtherScope
                    class MyClass @Inject constructor()
                    """
                        .trimIndent()
                )
            )
            .issues(MultipleScopesDetector.ISSUE)
            .run()
            .expect(
                """
                src/MyClass.kt:5: Error: Objects on the DI graph can only have one @Scope annotation, please remove one [LintError]
                class MyClass @Inject constructor()
                      ~~~~~~~
                1 errors, 0 warnings
            """
                    .trimIndent()
            )
            .expectErrorCount(1)
    }

    @Test
    fun `kotlin class with single scope does not trigger an error`() {
        TestLintTask.lint()
            .files(
                scopes,
                javaxAnnotations,
                TestFiles.kotlin(
                    """
                    import javax.inject.Inject

                    @MyScope
                    class MyClass @Inject constructor()
                    """
                        .trimIndent()
                )
            )
            .issues(MultipleScopesDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin class with no scopes does not trigger an error`() {
        TestLintTask.lint()
            .files(
                javaxAnnotations,
                TestFiles.kotlin(
                    """
                    import javax.inject.Inject

                    class MyClass @Inject constructor()
                    """
                        .trimIndent()
                )
            )
            .issues(MultipleScopesDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin module with multiple scopes triggers an error`() {
        TestLintTask.lint()
            .files(
                scopes,
                javaxAnnotations,
                daggerAnnotations,
                TestFiles.kotlin(
                    """
                    import dagger.Module
                    import dagger.Provides
                    import dagger.Binds
                    import javax.inject.Inject

                    interface MyInterface
                    class MyClass @Inject constructor(): MyInterface

                    @Module
                    interface MyModule {

                        @MyScope
                        @MyOtherScope
                        @Binds
                        fun bind(impl: MyClass): MyInterface

                       companion object {
                           @MyScope
                           @MyOtherScope
                           @Provides
                           fun provide(): MyClass = MyClass()
                       }
                    }
                    """
                        .trimIndent()
                )
            )
            .issues(MultipleScopesDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyInterface.kt:15: Error: Objects on the DI graph can only have one @Scope annotation, please remove one [LintError]
                        fun bind(impl: MyClass): MyInterface
                            ~~~~
                    src/MyInterface.kt:21: Error: Objects on the DI graph can only have one @Scope annotation, please remove one [LintError]
                           fun provide(): MyClass = MyClass()
                               ~~~~~~~
                    2 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(2)
    }

    @Test
    fun `kotlin module with single scope does not trigger an error`() {
        TestLintTask.lint()
            .files(
                scopes,
                javaxAnnotations,
                daggerAnnotations,
                TestFiles.kotlin(
                    """
                    import dagger.Module
                    import dagger.Provides
                    import dagger.Binds
                    import javax.inject.Inject

                    interface MyInterface
                    class MyClass @Inject constructor(): MyInterface

                    @Module
                    interface MyModule {

                        @MyScope
                        @Binds
                        fun bind(impl: MyClass): MyInterface

                       companion object {
                           @MyScope
                           @Provides
                           fun provide(): MyClass = MyClass()
                       }
                    }
                    """
                        .trimIndent()
                )
            )
            .issues(MultipleScopesDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin module with no scopes does not trigger an error`() {
        TestLintTask.lint()
            .files(
                scopes,
                javaxAnnotations,
                daggerAnnotations,
                TestFiles.kotlin(
                    """
                    import dagger.Module
                    import dagger.Provides
                    import dagger.Binds
                    import javax.inject.Inject

                    interface MyInterface
                    class MyClass @Inject constructor(): MyInterface

                    @Module
                    interface MyModule {

                        @Binds
                        fun bind(impl: MyClass): MyInterface

                       companion object {
                           @Provides
                           fun provide(): MyClass = MyClass()
                       }
                    }
                    """
                        .trimIndent()
                )
            )
            .issues(MultipleScopesDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `java class with multiple scopes triggers an error`() {
        TestLintTask.lint()
            .files(
                scopes,
                javaxAnnotations,
                TestFiles.java(
                    """
                    import javax.inject.Inject;

                    @MyScope
                    @MyOtherScope
                    class MyClass {
                        @Inject MyClass() {}
                    }
                    """
                        .trimIndent()
                )
            )
            .issues(MultipleScopesDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyClass.java:5: Error: Objects on the DI graph can only have one @Scope annotation, please remove one [LintError]
                    class MyClass {
                          ~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
    }

    @Test
    fun `java class with single scope does not trigger an error`() {
        TestLintTask.lint()
            .files(
                scopes,
                javaxAnnotations,
                TestFiles.java(
                    """
                    import javax.inject.Inject;

                    @MyScope
                    class MyClass {
                        @Inject MyClass() {}
                    }
                    """
                        .trimIndent()
                )
            )
            .issues(MultipleScopesDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `java class with no scopes does not trigger an error`() {
        TestLintTask.lint()
            .files(
                javaxAnnotations,
                TestFiles.java(
                    """
                    import javax.inject.Inject;

                   class MyClass {
                        @Inject MyClass() {}
                    }
                    """
                        .trimIndent()
                )
            )
            .issues(MultipleScopesDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `java @Provides method with multiple scopes triggers an error`() {
        TestLintTask.lint()
            .files(
                scopes,
                javaxAnnotations,
                daggerAnnotations,
                TestFiles.java(
                    """
                    import dagger.Module;
                    import dagger.Provides;

                    @Module
                    class MyModule {
                            class MyClass {}

                           @MyScope
                           @MyOtherScope
                           @Provides
                           MyClass provide() {
                                return MyClass;
                           }
                    }
                    """
                        .trimIndent()
                )
            )
            .issues(MultipleScopesDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyModule.java:11: Error: Objects on the DI graph can only have one @Scope annotation, please remove one [LintError]
                           MyClass provide() {
                                   ~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
    }

    @Test
    fun `java @Binds method with multiple scopes triggers an error`() {
        TestLintTask.lint()
            .files(
                scopes,
                javaxAnnotations,
                daggerAnnotations,
                TestFiles.java(
                    """
                    import dagger.Module;
                    import dagger.Binds;

                    @Module
                    interface MyModule {

                        interface MyInterface {}

                        class MyClass implements MyInterface {}

                        @MyScope
                        @MyOtherScope
                        @Binds
                        MyInterface bind(MyClass impl);
                    }
                    """
                        .trimIndent()
                )
            )
            .issues(MultipleScopesDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyModule.java:14: Error: Objects on the DI graph can only have one @Scope annotation, please remove one [LintError]
                        MyInterface bind(MyClass impl);
                                    ~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
    }

    @Test
    fun `java @Provides method with a single scope does not trigger an error`() {
        TestLintTask.lint()
            .files(
                scopes,
                javaxAnnotations,
                daggerAnnotations,
                TestFiles.java(
                    """
                    import dagger.Module;
                    import dagger.Provides;

                    @Module
                    class MyModule {
                            class MyClass {}

                           @MyScope
                           @Provides
                           MyClass provide() {
                                return MyClass;
                           }
                    }
                    """
                        .trimIndent()
                )
            )
            .issues(MultipleScopesDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `java @Binds method with single scope does not trigger an error`() {
        TestLintTask.lint()
            .files(
                scopes,
                javaxAnnotations,
                daggerAnnotations,
                TestFiles.java(
                    """
                    import dagger.Module;
                    import dagger.Binds;

                    @Module
                    interface MyModule {

                        interface MyInterface {}

                        class MyClass implements MyInterface {}

                        @MyScope
                        @Binds
                        MyInterface bind(MyClass impl);
                    }
                    """
                        .trimIndent()
                )
            )
            .issues(MultipleScopesDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `java @Provides method with no scopes does not trigger an error`() {
        TestLintTask.lint()
            .files(
                javaxAnnotations,
                daggerAnnotations,
                TestFiles.java(
                    """
                    import dagger.Module;
                    import dagger.Provides;

                    @Module
                    class MyModule {
                            class MyClass {}

                           @Provides
                           MyClass provide() {
                                return MyClass;
                           }
                    }
                    """
                        .trimIndent()
                )
            )
            .issues(MultipleScopesDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `java @Binds method with no scopes does not trigger an error`() {
        TestLintTask.lint()
            .files(
                javaxAnnotations,
                daggerAnnotations,
                TestFiles.java(
                    """
                    import dagger.Module;
                    import dagger.Binds;

                    @Module
                    interface MyModule {

                        interface MyInterface {}

                        class MyClass implements MyInterface {}

                        @Binds
                        MyInterface bind(MyClass impl);
                    }
                    """
                        .trimIndent()
                )
            )
            .issues(MultipleScopesDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }
}
