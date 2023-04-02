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
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import dev.whosnickdoglio.stubs.injectAnnotation
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class ConstructorInjectionOverFieldInjectionDetectorTest {

    @TestParameter lateinit var component: AndroidComponentParameters

    @Test
    fun `android component subclass in kotlin does not triggers member injection warning`() {
        TestLintTask.lint()
            .files(
                injectAnnotation,
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
                    .indented()
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
                injectAnnotation,
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
                    .indented()
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
                injectAnnotation,
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
                    .indented()
            )
            .issues(ConstructorInjectionOverFieldInjectionDetector.ISSUE)
            .run()
            .expect(
                """
                    src/com/test/android/Something.kt:9: Warning: Constructor injection should be favored over field injection for classes that support it. [ConstructorOverField]
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
                injectAnnotation,
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
                    .indented()
            )
            .issues(ConstructorInjectionOverFieldInjectionDetector.ISSUE)
            .run()
            .expect(
                """
                    src/com/test/android/Something.java:9: Warning: Constructor injection should be favored over field injection for classes that support it. [ConstructorOverField]
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
        val classImport: String
    ) {
        ACTIVITY("Activity", "android.app", "android.app.Activity"),
        APP_FRAGMENT("Fragment", "android.app", "android.app.Fragment"),
        APPLICATION("Application", "android.app", "android.app.Application"),
        SERVICE("Service", "android.app", "android.app.Service"),
        ANDROIDX_FRAGMENT("Fragment", "androidx.fragment.app", "androidx.fragment.app.Fragment"),
        BROADCAST_RECEIVER(
            "BroadcastReceiver",
            "android.content",
            "android.content.BroadcastReceiver"
        ),
        CONTENT_PROVIDER("ContentProvider", "android.content", "android.content.ContentProvider"),
    }
}
