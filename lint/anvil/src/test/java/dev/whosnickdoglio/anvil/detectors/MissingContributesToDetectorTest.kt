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
package dev.whosnickdoglio.anvil.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import dev.whosnickdoglio.stubs.daggerAnnotations
import org.junit.Test

class MissingContributesToDetectorTest {

    private val contributeTo =
        TestFiles.kotlin(
                """
            package com.squareup.anvil.annotations

            annotation class ContributesTo
        """
            )
            .indented()

    @Test
    fun `kotlin provides module without @ContributesTo annotation shows an error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                import dagger.Module
                import dagger.Provides

                @Module
                class MyModule {

                @Provides fun provideMyThing(): String = "Hello World"

                @Provides fun provideAnotherThing(): Int = 1

                }
            """
                    )
                    .indented()
            )
            .issues(MissingContributesToDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyModule.kt:5: Error: This Dagger module is missing a @ContributesTo annotation for Anvil to pick it up [MissingContributesToAnnotation]
                    class MyModule {
                          ~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                Fix for src/MyModule.kt line 5: Add @ContributesTo annotation:
                @@ -5 +5
                - class MyModule {
                + class @com.squareup.anvil.annotations.ContributesTo
                + MyModule {
            """
                    .trimIndent()
            )
    }

    @Test
    fun `kotlin provides module with @ContributesTo annotation shows no error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                contributeTo,
                TestFiles.kotlin(
                        """
                import dagger.Module
                import dagger.Provides
                import com.squareup.anvil.annotations.ContributesTo

                @Module
                @ContributesTo
                class MyModule {

                @Provides fun provideMyThing(): String = "Hello World"

                @Provides fun provideAnotherThing(): Int = 1

                }
            """
                    )
                    .indented()
            )
            .issues(MissingContributesToDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin @Binds module without @ContributesTo annotation shows an error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                contributeTo,
                TestFiles.kotlin(
                        """
                import dagger.Module
                import dagger.Binds

                interface MyThing
                class MyThingImpl: MyThing

                interface OtherThing
                class MyOtherThingImpl: MyOtherThingImpl

                @Module
                interface MyModule {

                @Binds fun provideMyThing(impl: MyThingImpl): MyThing

                @Binds fun provideMyOtherThing(impl: MyOtherThingImpl): OtherThing

                }
            """
                    )
                    .indented()
            )
            .issues(MissingContributesToDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyThing.kt:11: Error: This Dagger module is missing a @ContributesTo annotation for Anvil to pick it up [MissingContributesToAnnotation]
                    interface MyModule {
                              ~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                Fix for src/MyThing.kt line 11: Add @ContributesTo annotation:
                @@ -11 +11
                - interface MyModule {
                + interface @com.squareup.anvil.annotations.ContributesTo
                + MyModule {
            """
                    .trimIndent()
            )
    }

    @Test
    fun `kotlin @Binds module with @ContributesTo annotation shows no error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                contributeTo,
                TestFiles.kotlin(
                        """
                import dagger.Module
                import dagger.Binds
                import com.squareup.anvil.annotations.ContributesTo

                interface MyThing
                class MyThingImpl: MyThing

                interface OtherThing
                class MyOtherThingImpl: MyOtherThingImpl

                @Module
                @ContributesTo
                interface MyModule {

                @Binds fun provideMyThing(impl: MyThingImpl): MyThing

                 @Binds fun provideMyOtherThing(impl: MyOtherThingImpl): OtherThing

                }
            """
                    )
                    .indented()
            )
            .issues(MissingContributesToDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin companion object @Binds module with @ContributesTo annotation shows no error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                contributeTo,
                TestFiles.kotlin(
                        """
                import dagger.Module
                import dagger.Binds
                import com.squareup.anvil.annotations.ContributesTo

                interface MyThing
                class MyThingImpl: MyThing

                @Module
                @ContributesTo
                interface MyModule {

                    @Binds fun provideMyThing(impl: MyThingImpl): MyThing

                    companion object {
                        @Provides fun provideSomething(): String = "Hello world"
                    }
                }
            """
                    )
                    .indented()
            )
            .issues(MissingContributesToDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin companion object @Binds module without @ContributesTo annotation shows an error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                contributeTo,
                TestFiles.kotlin(
                        """
                import dagger.Module
                import dagger.Binds

                interface MyThing
                class MyThingImpl: MyThing

                @Module
                interface MyModule {

                    @Binds fun provideMyThing(impl: MyThingImpl): MyThing

                    companion object {
                        @Provides fun provideSomething(): String = "Hell World"
                    }
                }
            """
                    )
                    .indented()
            )
            .issues(MissingContributesToDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyThing.kt:8: Error: This Dagger module is missing a @ContributesTo annotation for Anvil to pick it up [MissingContributesToAnnotation]
                    interface MyModule {
                              ~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                Fix for src/MyThing.kt line 8: Add @ContributesTo annotation:
                @@ -8 +8
                - interface MyModule {
                + interface @com.squareup.anvil.annotations.ContributesTo
                + MyModule {
            """
                    .trimIndent()
            )
    }

    @Test
    fun `java provides module without @ContributesTo annotation shows an error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.java(
                        """
                import dagger.Module;
                import dagger.Provides;


                @Module
                class MyModule {

                    @Provides
                    String provideMyThing() {
                        return "Hello World";
                    }
                }
            """
                    )
                    .indented()
            )
            .issues(MissingContributesToDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `java @Binds module without @ContributesTo annotation shows no error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                contributeTo,
                TestFiles.java(
                        """
                import dagger.Module;
                import dagger.Binds;

                interface MyThing {}
                class MyThingImpl extends MyThing {}

                @Module
                interface MyModule {

                    @Binds
                    MyThing provideMyThing(MyThingImpl impl);
                }
            """
                    )
                    .indented()
            )
            .issues(MissingContributesToDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }
}
