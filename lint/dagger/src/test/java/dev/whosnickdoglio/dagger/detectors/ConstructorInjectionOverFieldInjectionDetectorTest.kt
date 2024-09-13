/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.dagger.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import dev.whosnickdoglio.stubs.javaxAnnotations
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class ConstructorInjectionOverFieldInjectionDetectorTest {
    @TestParameter lateinit var component: AndroidComponentParameters

    @Test
    fun `android component subclass in kotlin does not triggers member injection warning`() {
        TestLintTask.lint()
            .files(
                javaxAnnotations,
                TestFiles.kotlin(
                        """
                package ${component.classPackage}

                class ${component.className}

                """
                    )
                    .indented(),
                TestFiles.kotlin(
                        """
                package androidx

                import ${component.classImport}

                class AndroidX${component.className}: ${component.className}
                    """
                    )
                    .indented(),
                TestFiles.kotlin(
                        """
            package com.test.android

            import javax.inject.Inject
            import androidx.AndroidX${component.className}

            class Something

            class My${component.className}: AndroidX${component.className} {

            @Inject lateinit var something: Something

            }
                """
                    )
                    .indented(),
            )
            .issues(ConstructorInjectionOverFieldInjectionDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `android component subclass in java does not triggers member injection warning`() {
        TestLintTask.lint()
            .files(
                javaxAnnotations,
                TestFiles.java(
                        """
                package ${component.classPackage};

                class ${component.className} {}

                """
                    )
                    .indented(),
                TestFiles.java(
                        """
                package androidx;

                import ${component.classImport};

                class AndroidX${component.className} extends ${component.className} {}
                    """
                    )
                    .indented(),
                TestFiles.java(
                        """
            package com.test.android;

            import javax.inject.Inject;
            import androidx.AndroidX${component.className};

            class Something {}

            class My${component.className} extends AndroidX${component.className} {

            @Inject Something something;

            }
                """
                    )
                    .indented(),
            )
            .issues(ConstructorInjectionOverFieldInjectionDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin class that could use constructor injection triggers member injection warning`() {
        TestLintTask.lint()
            .files(
                javaxAnnotations,
                TestFiles.kotlin(
                        """
            package com.test.android

            import javax.inject.Inject

            class Something

            class MyClass {

            @Inject lateinit var something: Something

            }
                """
                    )
                    .indented(),
            )
            .issues(ConstructorInjectionOverFieldInjectionDetector.ISSUE)
            .run()
            .expect(
                """
                    src/com/test/android/Something.kt:9: Warning: Constructor injection should be favored over field injection for classes that support it.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#prefer-constructor-injection-over-field-injection for more information. [ConstructorOverField]
                    @Inject lateinit var something: Something
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    0 errors, 1 warnings
                """
                    .trimIndent()
            )
            .expectWarningCount(1)
    }

    @Test
    fun `java class that could use constructor injection triggers member injection warning`() {
        TestLintTask.lint()
            .files(
                javaxAnnotations,
                TestFiles.java(
                        """
            package com.test.android;

            import javax.inject.Inject;

            class Something {}

            class MyClass {

            @Inject Something something;

            }
                """
                    )
                    .indented(),
            )
            .issues(ConstructorInjectionOverFieldInjectionDetector.ISSUE)
            .run()
            .expect(
                """
                    src/com/test/android/Something.java:9: Warning: Constructor injection should be favored over field injection for classes that support it.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#prefer-constructor-injection-over-field-injection for more information. [ConstructorOverField]
                    @Inject Something something;
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    0 errors, 1 warnings
                """
                    .trimIndent()
            )
            .expectWarningCount(1)
    }

    @Suppress("unused")
    enum class AndroidComponentParameters(
        val className: String,
        val classPackage: String,
        val classImport: String,
    ) {
        ACTIVITY("Activity", "android.app", "android.app.Activity"),
        APP_FRAGMENT("Fragment", "android.app", "android.app.Fragment"),
        APPLICATION("Application", "android.app", "android.app.Application"),
        SERVICE("Service", "android.app", "android.app.Service"),
        ANDROIDX_FRAGMENT("Fragment", "androidx.fragment.app", "androidx.fragment.app.Fragment"),
        BROADCAST_RECEIVER(
            "BroadcastReceiver",
            "android.content",
            "android.content.BroadcastReceiver",
        ),
        CONTENT_PROVIDER("ContentProvider", "android.content", "android.content.ContentProvider"),
    }
}
