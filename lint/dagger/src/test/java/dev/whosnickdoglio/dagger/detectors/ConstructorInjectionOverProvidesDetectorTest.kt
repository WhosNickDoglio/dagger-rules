/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.dagger.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import dev.whosnickdoglio.stubs.daggerAnnotations
import org.junit.Test

class ConstructorInjectionOverProvidesDetectorTest {

    @Test
    fun `class that could use constructor injection triggers provides warning`() {
        lint()
            .files(
                daggerAnnotations,
                kotlin(
                        """
            package com.test.android

            import dagger.Provides
            import dagger.Module

            class MyClass

            @Module
            object MyModule {

                @Provides
                fun provideMyClass(): MyClass {
                    return MyClass()
                }
            }
                """
                    )
                    .indented()
            )
            .issues(ConstructorInjectionOverProvidesDetector.ISSUE)
            .run()
            .expectClean()
    }
}
