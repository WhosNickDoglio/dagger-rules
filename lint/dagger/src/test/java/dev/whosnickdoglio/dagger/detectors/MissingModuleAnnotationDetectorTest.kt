/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.dagger.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import dev.whosnickdoglio.stubs.daggerAnnotations
import org.junit.Test

class MissingModuleAnnotationDetectorTest {

    @Test
    fun `kotlin @Module with @Provides method and without annotation shows an error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                package com.test.android

                import dagger.Provides

                 class MyModule {

                        @Provides
                        fun doSomething(): String = "Hello"
                }
                """
                    )
                    .indented()
            )
            .issues(MissingModuleAnnotationDetector.ISSUE)
            .run()
            .expect(
                """
                    src/com/test/android/MyModule.kt:5: Error: Provides or binds methods won't be picked up if the class isn't annotated with @Module.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#classes-with-provides-binds-or-multibinds-methods-should-be-annotated-with-module for more information. [MissingModuleAnnotation]
                     class MyModule {
                           ~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
        //            .expectFixDiffs("""
        //
        //            """.trimIndent())
    }

    @Test
    fun `kotlin @Module with @Provides method and annotation shows no error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                package com.test.android

                import dagger.Provides
                import dagger.Module

                 @Module
                 class MyModule {

                        @Provides
                        fun doSomething(): String = "Hello"
                }
                """
                    )
                    .indented()
            )
            .issues(MissingModuleAnnotationDetector.ISSUE)
            .run()
            .expectErrorCount(0)
            .expectClean()
    }

    @Test
    fun `kotlin @Module with @Binds method and no module annotation shows an error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                 interface PizzaMaker
                 class PizzaMakerImpl: PizzaMaker
                """
                    )
                    .indented(),
                TestFiles.kotlin(
                        """
                package com.test.android

                import dagger.Binds

                 interface MyModule {

                        @Binds
                        fun doSomething(impl: PizzaMakerImpl): PizzaMaker
                }
                """
                    )
                    .indented()
            )
            .issues(MissingModuleAnnotationDetector.ISSUE)
            .run()
            .expect(
                """
                    src/com/test/android/MyModule.kt:5: Error: Provides or binds methods won't be picked up if the class isn't annotated with @Module.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#classes-with-provides-binds-or-multibinds-methods-should-be-annotated-with-module for more information. [MissingModuleAnnotation]
                     interface MyModule {
                               ~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
    }

    @Test
    fun `kotlin @Module with @Binds method and module annotation shows no error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                 interface PizzaMaker
                 class PizzaMakerImpl
                """
                    )
                    .indented(),
                TestFiles.kotlin(
                        """
                package com.test.android

                import dagger.Binds
                import dagger.Module

                 @Module
                 interface MyModule {

                        @Binds
                        fun doSomething(impl: PizzaMakerImpl): PizzaMaker
                }
                """
                    )
                    .indented()
            )
            .issues(MissingModuleAnnotationDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `kotlin @Module that uses companion object without module annotation shows an error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                 interface PizzaMaker
                 class PizzaMakerImpl
                """
                    )
                    .indented(),
                TestFiles.kotlin(
                        """
                package com.test.android

                import dagger.Binds
                import dagger.Provides

                 interface MyModule {

                        @Binds
                        fun doSomething(impl: PizzaMakerImpl): PizzaMaker

                        companion object {
                            @Provides fun provideMyThing(): String = "Hello World"
                        }
                }
                """
                    )
                    .indented()
            )
            .issues(MissingModuleAnnotationDetector.ISSUE)
            .run()
            .expect(
                """
                    src/com/test/android/MyModule.kt:6: Error: Provides or binds methods won't be picked up if the class isn't annotated with @Module.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#classes-with-provides-binds-or-multibinds-methods-should-be-annotated-with-module for more information. [MissingModuleAnnotation]
                     interface MyModule {
                               ~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
    }

    @Test
    fun `kotlin @Module that uses companion object without module annotation shows no error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                 interface PizzaMaker
                 class PizzaMakerImpl
                """
                    )
                    .indented(),
                TestFiles.kotlin(
                        """
                package com.test.android

                import dagger.Binds
                import dagger.Provides
                import dagger.Module

                 @Module
                 interface MyModule {

                        @Binds
                        fun doSomething(impl: PizzaMakerImpl): PizzaMaker

                        companion object {
                            @Provides fun provideMyThing(): String = "Hello World"
                        }
                }
                """
                    )
                    .indented()
            )
            .issues(MissingModuleAnnotationDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `java @Module with @Provides method and without annotation shows an error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.java(
                        """
                package com.test.android;

                import dagger.Provides;

                 class MyModule {

                        @Provides
                        String doSomething() {
                            return "Hell World";
                        }
                }
                """
                    )
                    .indented()
            )
            .issues(MissingModuleAnnotationDetector.ISSUE)
            .run()
            .expect(
                """
                    src/com/test/android/MyModule.java:5: Error: Provides or binds methods won't be picked up if the class isn't annotated with @Module.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#classes-with-provides-binds-or-multibinds-methods-should-be-annotated-with-module for more information. [MissingModuleAnnotation]
                     class MyModule {
                           ~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
        //            .expectFixDiffs("""
        //
        //            """.trimIndent())
    }

    @Test
    fun `java @Module with @Provides method and annotation shows no error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.java(
                        """
                package com.test.android;

                import dagger.Provides;
                import dagger.Module;

                 @Module
                 class MyModule {

                        @Provides
                        String doSomething() {
                            return "Hello World";
                        }
                }
                """
                    )
                    .indented()
            )
            .issues(MissingModuleAnnotationDetector.ISSUE)
            .run()
            .expectErrorCount(0)
            .expectClean()
    }

    @Test
    fun `java @Module with @Binds method and no module annotation shows an error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                 interface PizzaMaker
                 class PizzaMakerImpl: PizzaMaker
                """
                    )
                    .indented(),
                TestFiles.java(
                        """
                package com.test.android;

                import dagger.Binds;

                 interface MyModule {

                        @Binds
                        PizzaMaker doSomething(PizzaMakerImpl impl);
                }
                """
                    )
                    .indented()
            )
            .issues(MissingModuleAnnotationDetector.ISSUE)
            .run()
            .expect(
                """
                    src/com/test/android/MyModule.java:5: Error: Provides or binds methods won't be picked up if the class isn't annotated with @Module.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#classes-with-provides-binds-or-multibinds-methods-should-be-annotated-with-module for more information. [MissingModuleAnnotation]
                     interface MyModule {
                               ~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
    }

    @Test
    fun `java @Module with @Binds method and module annotation shows no error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                 interface PizzaMaker
                 class PizzaMakerImpl
                """
                    )
                    .indented(),
                TestFiles.java(
                        """
                package com.test.android;

                import dagger.Binds;
                import dagger.Module;

                 @Module
                 interface MyModule {

                        @Binds
                        PizzaMaker doSomething(PizzaMakerImpl impl);
                }
                """
                    )
                    .indented()
            )
            .issues(MissingModuleAnnotationDetector.ISSUE)
            .run()
            .expectClean()
    }
}
