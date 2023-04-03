/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.dagger.detectors

class FavorBindsOverProvidesDetectorTest {

    //    @Test
    //    fun `class that could use a binds method over provides method triggers warning`() {
    //        TestLintTask.lint()
    //            .files(
    //                daggerAnnotations,
    //                TestFiles.kotlin(
    //                        """
    //                    package com.test.android
    //
    //                    import dagger.Provides
    //                    import dagger.Module
    //
    //                    interface Greeter
    //
    //                    class GreeterImpl: Greeter
    //
    //                    @Module
    //                    object MyModule {
    //
    //                        @Provides
    //                        fun provideGreeter(): Greeter = GreeterImpl()
    //
    //                        @Provides
    //                        fun provideAnotherGreeter(): Greeter {
    //                            return GreeterImpl()
    //                        }
    //                    }
    //                        """
    //                    )
    //                    .indented()
    //            )
    //            .issues(FavorBindsOverProvidesDetector.ISSUE)
    //            .run()
    //            .expectErrorCount(1)
    //            .expect(
    //                """
    //
    //            """
    //                    .trimIndent()
    //            )
    //    }
}
