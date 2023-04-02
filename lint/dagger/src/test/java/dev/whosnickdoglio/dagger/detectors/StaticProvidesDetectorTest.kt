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
import dev.whosnickdoglio.stubs.daggerAnnotations
import org.junit.Test

class StaticProvidesDetectorTest {

    // todo companion object tests
    // TODO multiple @Provides/@Binds

    @Test
    fun `kotlin @Provides methods in an object do not show a warning`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                package com.test.android

                import dagger.Provides
                import dagger.Module

                 @Module
                 object MyModule {

                        @Provides
                        fun myString(): String = "Hello World"
                }
                """
                    )
                    .indented()
            )
            .issues(StaticProvidesDetector.ISSUE)
            .run()
            .expect(
                """
                src/com/test/android/MyModule.kt:9: Warning: plz use static provides methods [StaticProvides]
                        @Provides
                        ^
                0 errors, 1 warnings
            """
                    .trimIndent()
            )
            .expectWarningCount(1)
    }

    @Test
    fun `kotlin @Provides methods in a class show a warning`() {
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
                        fun myString(): String = "Hello World"
                }
                """
                    )
                    .indented()
            )
            .issues(StaticProvidesDetector.ISSUE)
            .run()
            .expect(
                """
                src/com/test/android/MyModule.kt:9: Warning: plz use static provides methods [StaticProvides]
                        @Provides
                        ^
                0 errors, 1 warnings
            """
                    .trimIndent()
            )
            .expectWarningCount(1)
    }

    @Test
    fun `java @Provides methods that are static do not show a warning`() {
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
                        public static String myString() {
                            return "My String";
                        }

                }
                """
                    )
                    .indented()
            )
            .issues(StaticProvidesDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `java @Provides methods that are not static show a warning`() {
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
                        public String myString() {
                            return "My String";
                        }

                }
                """
                    )
                    .indented()
            )
            .issues(StaticProvidesDetector.ISSUE)
            .run()
            .expect(
                """
                src/com/test/android/MyModule.java:10: Warning: plz use static provides methods [StaticProvides]
                        public String myString() {
                                      ~~~~~~~~
                0 errors, 1 warnings
            """
                    .trimIndent()
            )
            .expectWarningCount(1)
    }
}
