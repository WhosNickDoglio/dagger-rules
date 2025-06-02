/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.anvil.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import dev.whosnickdoglio.stubs.anvilAnnotations
import dev.whosnickdoglio.stubs.daggerAnnotations
import org.junit.Test

class MissingContributesToDetectorTest {
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
                    .indented(),
            )
            .issues(MissingContributesToDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyModule.kt:5: Error: This Dagger module is missing a @ContributesTo annotation for Anvil to pick it up. See https://whosnickdoglio.dev/dagger-rules/rules/#a-class-annotated-with-module-should-also-be-annotated-with-contributesto for more information. [MissingContributesToAnnotation]
                    class MyModule {
                          ~~~~~~~~
                    1 errors, 0 warnings
                """
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                    Autofix for src/MyModule.kt line 5: Add @ContributesTo annotation:
                    @@ -4 +4
                    + @com.squareup.anvil.annotations.ContributesTo
                """
            )
    }

    @Test
    fun `kotlin provides module with @ContributesTo annotation shows no error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                anvilAnnotations,
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
                    .indented(),
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
                anvilAnnotations,
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
                    .indented(),
            )
            .issues(MissingContributesToDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyThing.kt:11: Error: This Dagger module is missing a @ContributesTo annotation for Anvil to pick it up. See https://whosnickdoglio.dev/dagger-rules/rules/#a-class-annotated-with-module-should-also-be-annotated-with-contributesto for more information. [MissingContributesToAnnotation]
                    interface MyModule {
                              ~~~~~~~~
                    1 errors, 0 warnings
                """
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                    Autofix for src/MyThing.kt line 11: Add @ContributesTo annotation:
                    @@ -10 +10
                    + @com.squareup.anvil.annotations.ContributesTo
                """
            )
    }

    @Test
    fun `kotlin @Binds module with @ContributesTo annotation shows no error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                anvilAnnotations,
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
                    .indented(),
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
                anvilAnnotations,
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
                    .indented(),
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
                anvilAnnotations,
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
                    .indented(),
            )
            .issues(MissingContributesToDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyThing.kt:8: Error: This Dagger module is missing a @ContributesTo annotation for Anvil to pick it up. See https://whosnickdoglio.dev/dagger-rules/rules/#a-class-annotated-with-module-should-also-be-annotated-with-contributesto for more information. [MissingContributesToAnnotation]
                    interface MyModule {
                              ~~~~~~~~
                    1 errors, 0 warnings
                """
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                    Autofix for src/MyThing.kt line 8: Add @ContributesTo annotation:
                    @@ -7 +7
                    + @com.squareup.anvil.annotations.ContributesTo
                """
            )
    }

    @Test
    fun `kotlin @Module without @ContributesTo with lint option set shows error with expected quickfix`() {
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
                    .indented(),
            )
            .issues(MissingContributesToDetector.ISSUE)
            .configureOption(
                MissingContributesToDetector.CUSTOM_ANVIL_SCOPE_OPTION_KEY,
                "dev.whosnickdoglio.anvil.AppScope",
            )
            .run()
            .expect(
                """
                    src/MyModule.kt:5: Error: This Dagger module is missing a @ContributesTo annotation for Anvil to pick it up. See https://whosnickdoglio.dev/dagger-rules/rules/#a-class-annotated-with-module-should-also-be-annotated-with-contributesto for more information. [MissingContributesToAnnotation]
                    class MyModule {
                          ~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                    Autofix for src/MyModule.kt line 5: Contribute to AppScope :
                    @@ -4 +4
                    + @com.squareup.anvil.annotations.ContributesTo(dev.whosnickdoglio.anvil.AppScope::class)
                """
                    .trimIndent()
            )
    }

    @Test
    fun `java provides module without @ContributesTo annotation shows no error`() {
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
                    .indented(),
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
                anvilAnnotations,
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
                    .indented(),
            )
            .issues(MissingContributesToDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }
}
