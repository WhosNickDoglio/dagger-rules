/*
 * MIT License
 *
 * Copyright (c) 2023 Nicholas Doglio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.whosnickdoglio.dagger.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import org.junit.Test

class MissingModuleAnnotationTest {
    // TODO java tests

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
            .issues(MissingModuleAnnotation.ISSUE)
            .run()
            .expect(
                """
                    src/com/test/android/MyModule.kt:5: Error: Don't forget the @Module annotation! [MissingModuleAnnotation]
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
            .issues(MissingModuleAnnotation.ISSUE)
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
            .issues(MissingModuleAnnotation.ISSUE)
            .run()
            .expect(
                """
                    src/com/test/android/MyModule.kt:5: Error: Don't forget the @Module annotation! [MissingModuleAnnotation]
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
            .issues(MissingModuleAnnotation.ISSUE)
            .run()
            .expectClean()
    }
}
