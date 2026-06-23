/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.dagger.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import dev.whosnickdoglio.stubs.daggerAnnotations
import org.junit.Test

class FavorBindsOverProvidesDetectorTest {

    // TODO add more complicated methods (multiple params, setup before calling constructor,
    // returning a lambda etc)
    //  or valid @Provides use cases

    @Test
    fun `kotlin returning @Provides method parameter directly with supertype as return type shows an warning`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                                import dagger.Provides
                                import dagger.Module

                                interface Greeter
                                class GreeterImpl: Greeter

                                @Module
                                object MyModule {

                                    @Provides
                                    fun provideAnotherGreeter(impl: GreeterImpl): Greeter = impl
                                }
                                    """
                    )
                    .indented(),
            )
            .issues(FavorBindsOverProvidesDetector.ISSUE)
            .run()
            .expect(
                """
                src/Greeter.kt:11: Warning: plz use @Binds instead of @Provides [FavorBindsOverProvides]
                    fun provideAnotherGreeter(impl: GreeterImpl): Greeter = impl
                        ~~~~~~~~~~~~~~~~~~~~~
                0 errors, 1 warnings
            """
                    .trimIndent()
            )
            .expectWarningCount(1)
    }

    @Test
    fun `java returning @Provides method parameter directly with supertype as return type shows an warning`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.java(
                        """
                                import dagger.Provides;
                                import dagger.Module;

                                interface Greeter {}
                                class GreeterImpl extends Greeter {}

                                @Module
                                class MyModule {

                                    @Provides
                                    Greeter provideAnotherGreeter(GreeterImpl impl) {
                                        return impl;
                                    }
                                }
                                    """
                    )
                    .indented(),
            )
            .issues(FavorBindsOverProvidesDetector.ISSUE)
            .run()
            .expect(
                """
                src/Greeter.java:11: Warning: plz use @Binds instead of @Provides [FavorBindsOverProvides]
                    Greeter provideAnotherGreeter(GreeterImpl impl) {
                            ~~~~~~~~~~~~~~~~~~~~~
                0 errors, 1 warnings
            """
                    .trimIndent()
            )
            .expectWarningCount(1)
    }

    @Test
    fun `kotlin calling constructor directly in @Provides method instead of using @Binds method shows a warning`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                            import dagger.Provides
                            import dagger.Module

                            interface Greeter
                            class GreeterImpl: Greeter

                            @Module
                            object MyModule {

                                @Provides fun provideGreeter(): Greeter = GreeterImpl()
                            }
                                """
                    )
                    .indented(),
            )
            .issues(FavorBindsOverProvidesDetector.ISSUE)
            .run()
            .expect("")
            .expectWarningCount(1)
    }

    @Test
    fun `java calling constructor directly in @Provides method instead of using @Binds method shows a warning`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.java(
                        """
                            import dagger.Provides;
                            import dagger.Module;

                            interface Greeter {}
                            class GreeterImpl implements Greeter {}

                            @Module
                            class MyModule {

                                @Provides Greeter provideGreeter() {
                                    return GreeterImpl();
                                }
                            }
                                """
                    )
                    .indented(),
            )
            .issues(FavorBindsOverProvidesDetector.ISSUE)
            .run()
            .expectErrorCount(1)
            .expect("")
    }

    @Test
    fun `kotlin @Provides method that uses a Builder does not show a warning`() {
        TODO("Not yet implemented")
    }

    @Test
    fun `java @Provides method that uses a Builder does not show a warning`() {
        TODO("Not yet implemented")
    }

    @Test
    fun `kotlin @Provides method that uses a Factory does not show a warning`() {
        TODO("Not yet implemented")
    }

    @Test
    fun `java @Provides method that uses a Factory does not show a warning`() {
        TODO("Not yet implemented")
    }
}
