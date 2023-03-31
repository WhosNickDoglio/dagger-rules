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
package dev.whosnickdoglio.hilt.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import dev.whosnickdoglio.hilt.ANDROID_ENTRY_POINT
import dev.whosnickdoglio.hilt.HILT_ANDROID_APP
import dev.whosnickdoglio.hilt.HILT_VIEW_MODEL
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class MissingHiltAnnotationDetectorTest {

    @TestParameter lateinit var parameter: HiltAnnotationParameter

    @Test
    fun `java android component with hilt annotation does not trigger error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.java(
                        """
                package ${parameter.classPackage};

                class ${parameter.className} {}

                """
                    )
                    .indented(),
                TestFiles.java(
                        """
                package androidx;

                import ${parameter.classPackage}.${parameter.className};
                import ${parameter.annotation};

                @${parameter.annotation.substringAfterLast(".")}
                class AndroidX${parameter.className} extends ${parameter.className} {}
                    """
                    )
                    .indented(),
            )
            .issues(MissingHiltAnnotationDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `java android component without hilt annotation triggers error`() {
        fun expectedErrorMessage(): String {

            val errorHighlight =
                "AndroidX${parameter.className}".map { "~" }.joinToString(separator = "")

            return """
                src/androidx/AndroidX${parameter.className}.java:6: Error:  [MissingHiltAnnotation]
                class AndroidX${parameter.className} extends ${parameter.className} {}
                      $errorHighlight
                1 errors, 0 warnings
            """
        }

        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.java(
                        """
                package ${parameter.classPackage};

                class ${parameter.className} {}

                """
                    )
                    .indented(),
                TestFiles.java(
                        """
                package androidx;

                import ${parameter.classPackage}.${parameter.className};


                class AndroidX${parameter.className} extends ${parameter.className} {}
                    """
                    )
                    .indented(),
            )
            .issues(MissingHiltAnnotationDetector.ISSUE)
            .run()
            .expect(expectedErrorMessage())
            .expectErrorCount(1)
    }

    @Test
    fun `kotlin android component with hilt annotation does not trigger error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                        """
                package ${parameter.classPackage}

                class ${parameter.className}

                """
                    )
                    .indented(),
                TestFiles.kotlin(
                        """
                package androidx

                import ${parameter.classPackage}.${parameter.className}
                import ${parameter.annotation}

                @${parameter.annotation.substringAfterLast(".")}
                class AndroidX${parameter.className}: ${parameter.className}
                    """
                    )
                    .indented(),
            )
            .issues(MissingHiltAnnotationDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin android component without hilt annotation triggers error`() {
        fun expectedErrorMessage(): String {

            val errorHighlight =
                "AndroidX${parameter.className}".map { "~" }.joinToString(separator = "")

            return """
                src/androidx/AndroidX${parameter.className}.kt:6: Error:  [MissingHiltAnnotation]
                class AndroidX${parameter.className} : ${parameter.className}
                      $errorHighlight
                1 errors, 0 warnings
            """
        }

        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                        """
                package ${parameter.classPackage}

                class ${parameter.className}

                """
                    )
                    .indented(),
                TestFiles.kotlin(
                        """
                package androidx;

                import ${parameter.classPackage}.${parameter.className}


                class AndroidX${parameter.className} : ${parameter.className}
                    """
                    )
                    .indented(),
            )
            .issues(MissingHiltAnnotationDetector.ISSUE)
            .run()
            .expect(expectedErrorMessage())
            .expectErrorCount(1)
    }

    @Suppress("unused")
    enum class HiltAnnotationParameter(
        val className: String,
        val classPackage: String,
        val annotation: String,
    ) {
        ACTIVITY("Activity", "android.app", ANDROID_ENTRY_POINT),
        APP_FRAGMENT("Fragment", "android.app", ANDROID_ENTRY_POINT),
        APPLICATION("Application", "android.app", HILT_ANDROID_APP),
        SERVICE("Service", "android.app", ANDROID_ENTRY_POINT),
        ANDROIDX_FRAGMENT("Fragment", "androidx.fragment.app", ANDROID_ENTRY_POINT),
        BROADCAST_RECEIVER("BroadcastReceiver", "android.content", ANDROID_ENTRY_POINT),
        CONTENT_PROVIDER("ContentProvider", "android.content", ANDROID_ENTRY_POINT),
        VIEWMODEL("ViewModel", "androidx.lifecycle", HILT_VIEW_MODEL)
    }
}
