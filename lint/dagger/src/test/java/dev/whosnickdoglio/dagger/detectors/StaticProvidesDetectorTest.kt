/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.dagger.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import dev.whosnickdoglio.stubs.daggerAnnotations
import org.junit.Test

class StaticProvidesDetectorTest {

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
                        @Provides fun myString(): String = "Hello World"
                        @Provides fun myInt(): Int = 1
                }
                """
                    )
                    .indented()
            )
            .issues(StaticProvidesDetector.ISSUE)
            .run()
            .expectClean()
            .expectWarningCount(0)
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
                        @Provides fun myString(): String = "Hello World"
                        @Provides fun myInt(): Int = 1
                }
                """
                    )
                    .indented()
            )
            .issues(StaticProvidesDetector.ISSUE)
            .run()
            .expect(
                """
                    src/com/test/android/MyModule.kt:8: Warning: plz use static provides methods [StaticProvides]
                            @Provides fun myString(): String = "Hello World"
                            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    src/com/test/android/MyModule.kt:9: Warning: plz use static provides methods [StaticProvides]
                            @Provides fun myInt(): Int = 1
                            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    0 errors, 2 warnings
                """
                    .trimIndent()
            )
            .expectWarningCount(2)
    }

    @Test
    fun `kotlin companion object with @Binds and @Provides method does not show warning`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                package com.test.android

                import dagger.Provides
                import dagger.Module

                interface Repository
                class SqlRepository: Repository

                 @Module
                 interface MyModule {

                 @Binds fun bindsRepository(impl: SqlRepository): Repository

                 companion object {
                        @Provides fun myString(): String = "Hello World"
                        @Provides fun myInt(): Int = 1
                    }
                }
                """
                    )
                    .indented()
            )
            .issues(StaticProvidesDetector.ISSUE)
            .run()
            .expectClean()
            .expectWarningCount(0)
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

                        @Provides
                        public static Long myLong() {
                            return 100L;
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

                        @Provides
                        public Long myLong() {
                            return 100L;
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
                    src/com/test/android/MyModule.java:15: Warning: plz use static provides methods [StaticProvides]
                            public Long myLong() {
                                        ~~~~~~
                    0 errors, 2 warnings
                """
                    .trimIndent()
            )
            .expectWarningCount(2)
    }
}
