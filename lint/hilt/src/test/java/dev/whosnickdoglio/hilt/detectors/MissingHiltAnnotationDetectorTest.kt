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
import dev.whosnickdoglio.stubs.injectAnnotation
import org.junit.Test
import org.junit.runner.RunWith

@Suppress("JUnitMalformedDeclaration")
@RunWith(TestParameterInjector::class)
class MissingHiltAnnotationDetectorTest {

    private val viewModelStub =
        TestFiles.kotlin(
                """
                        package androidx.lifecycle
                        class ViewModel
                    """
            )
            .indented()

    @Test
    fun `java application class without @HiltAndroidApp triggers error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                        """
                    package android.app
                    class Application
                """
                    )
                    .indented(),
                TestFiles.java(
                        """
                import android.app.Application;

                class MyApplication extends Application {}
            """
                    )
                    .indented()
            )
            .issues(MissingHiltAnnotationDetector.ISSUE)
            .run()
            .expect(
                """
                src/MyApplication.java:3: Error: This class is missing the @HiltAndroidApp [MissingHiltAnnotation]
                class MyApplication extends Application {}
                      ~~~~~~~~~~~~~
                1 errors, 0 warnings
            """
                    .trimIndent()
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                Fix for src/MyApplication.java line 3: Add HiltAndroidApp annotation:
                @@ -3 +3
                - class MyApplication extends Application {}
                @@ -4 +3
                + class @dagger.hilt.android.HiltAndroidApp
                + MyApplication extends Application {}
            """
                    .trimIndent()
            )
    }

    @Test
    fun `java application class with @HiltAndroidApp does not triggers error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                        """
                    package android.app
                    class Application
                """
                    )
                    .indented(),
                TestFiles.java(
                        """
                import android.app.Application;
                import $HILT_ANDROID_APP;

                @${HILT_ANDROID_APP.substringAfterLast(".")}
                class MyApplication extends Application {}
            """
                    )
                    .indented()
            )
            .issues(MissingHiltAnnotationDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin application class without @HiltAndroidApp triggers error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                        """
                    package android.app
                    class Application
                """
                    )
                    .indented(),
                TestFiles.kotlin(
                        """
                import android.app.Application

                class MyApplication : Application
            """
                    )
                    .indented()
            )
            .issues(MissingHiltAnnotationDetector.ISSUE)
            .run()
            .expect(
                """
                src/MyApplication.kt:3: Error: This class is missing the @HiltAndroidApp [MissingHiltAnnotation]
                class MyApplication : Application
                      ~~~~~~~~~~~~~
                1 errors, 0 warnings
            """
                    .trimIndent()
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                    Fix for src/MyApplication.kt line 3: Add HiltAndroidApp annotation:
                    @@ -3 +3
                    - class MyApplication : Application
                    @@ -4 +3
                    + class @dagger.hilt.android.HiltAndroidApp
                    + MyApplication : Application
                """
                    .trimIndent()
            )
    }

    @Test
    fun `kotlin application class with @HiltAndroidApp does not triggers error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                        """
                    package android.app
                    class Application
                """
                    )
                    .indented(),
                TestFiles.kotlin(
                        """
                import android.app.Application
                import $HILT_ANDROID_APP

                @${HILT_ANDROID_APP.substringAfterLast(".")}
                class MyApplication : Application
            """
                    )
                    .indented()
            )
            .issues(MissingHiltAnnotationDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin ViewModel with @Inject and no @HiltViewModel triggers error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                injectAnnotation,
                viewModelStub,
                TestFiles.kotlin(
                        """
                import androidx.lifecycle.ViewModel
                import javax.inject.Inject

                class MyViewModel @Inject constructor(
                    private val something: String
                ) : ViewModel()
            """
                    )
                    .indented()
            )
            .issues(MissingHiltAnnotationDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyViewModel.kt:4: Error: This class is missing the @HiltViewModel [MissingHiltAnnotation]
                    class MyViewModel @Inject constructor(
                          ~~~~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                    Fix for src/MyViewModel.kt line 4: Add HiltViewModel annotation:
                    @@ -4 +4
                    - class MyViewModel @Inject constructor(
                    + class @dagger.hilt.android.lifecycle.HiltViewModel
                    + MyViewModel @Inject constructor(
                """
                    .trimIndent()
            )
    }

    @Test
    fun `java ViewModel with @Inject and no @HiltViewModel triggers error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                injectAnnotation,
                viewModelStub,
                TestFiles.java(
                        """
                import androidx.lifecycle.ViewModel;
                import javax.inject.Inject;

                class MyViewModel extends ViewModel {

                    private final String something;

                    @Inject
                    public MyViewModel(String something) {
                        this.something = something;
                    }
                }
            """
                    )
                    .indented()
            )
            .issues(MissingHiltAnnotationDetector.ISSUE)
            .run()
            .expect(
                """
                src/MyViewModel.java:4: Error: This class is missing the @HiltViewModel [MissingHiltAnnotation]
                class MyViewModel extends ViewModel {
                      ~~~~~~~~~~~
                1 errors, 0 warnings
            """
                    .trimIndent()
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                Fix for src/MyViewModel.java line 4: Add HiltViewModel annotation:
                @@ -4 +4
                - class MyViewModel extends ViewModel {
                + class @dagger.hilt.android.lifecycle.HiltViewModel
                + MyViewModel extends ViewModel {
            """
                    .trimIndent()
            )
    }

    @Test
    fun `kotlin ViewModel with no @Inject or @HiltViewModel does not triggers error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                viewModelStub,
                TestFiles.kotlin(
                        """
                import androidx.lifecycle.ViewModel

                class MyViewModel : ViewModel()
            """
                    )
                    .indented()
            )
            .issues(MissingHiltAnnotationDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `java ViewModel with no @Inject or @HiltViewModel does not triggers error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                viewModelStub,
                TestFiles.java(
                        """
                import androidx.lifecycle.ViewModel;

                class MyViewModel extends ViewModel {}
            """
                    )
                    .indented()
            )
            .issues(MissingHiltAnnotationDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin ViewModel with @HiltViewModel does not triggers error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                viewModelStub,
                TestFiles.kotlin(
                        """
                import androidx.lifecycle.ViewModel
                import $HILT_VIEW_MODEL

                @${HILT_VIEW_MODEL.substringAfterLast(".")}
                class MyViewModel : ViewModel()
            """
                    )
                    .indented()
            )
            .issues(MissingHiltAnnotationDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `java ViewModel with @HiltViewModel does not triggers error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                viewModelStub,
                TestFiles.java(
                        """
                import androidx.lifecycle.ViewModel;
                import $HILT_VIEW_MODEL;

                @${HILT_VIEW_MODEL.substringAfterLast(".")}
                class MyViewModel extends ViewModel {}
            """
                    )
                    .indented()
            )
            .issues(MissingHiltAnnotationDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin ViewModel with @HiltViewModel and @Inject does not triggers error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                viewModelStub,
                injectAnnotation,
                TestFiles.kotlin(
                        """
                import androidx.lifecycle.ViewModel
                import javax.inject.Inject
                import $HILT_VIEW_MODEL

                @${HILT_VIEW_MODEL.substringAfterLast(".")}
                class MyViewModel @Inject constructor() : ViewModel()
            """
                    )
                    .indented()
            )
            .issues(MissingHiltAnnotationDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `java ViewModel with @HiltViewModel and @Inject does not triggers error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                injectAnnotation,
                viewModelStub,
                TestFiles.java(
                        """
                import androidx.lifecycle.ViewModel;
                import javax.inject.Inject;
                import $HILT_VIEW_MODEL;

                @${HILT_VIEW_MODEL.substringAfterLast(".")}
                class MyViewModel extends ViewModel {

                    @Inject
                    public MyViewModel() {}
                }
            """
                    )
                    .indented()
            )
            .issues(MissingHiltAnnotationDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    // TODO @Inject

    @Test
    fun `java android component using field injection with @AndroidEntryPoint does not trigger error`(
        @TestParameter(
            value =
                [
                    "android.app.Activity",
                    "android.app.Fragment",
                    "android.app.Service",
                    "android.content.ContentProvider",
                    "android.content.BroadcastReceiver",
                    "androidx.fragment.app.Fragment"
                ]
        )
        androidEntryPoint: String
    ) {
        val classPackage = androidEntryPoint.substringBeforeLast(".")
        val className = androidEntryPoint.substringAfterLast(".")

        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.java(
                        """
                package $classPackage;

                class $className {}

                """
                    )
                    .indented(),
                TestFiles.java(
                        """
                package androidx;

                import $androidEntryPoint;
                import $ANDROID_ENTRY_POINT;

                @${ANDROID_ENTRY_POINT.substringAfterLast(".")}
                class AndroidX$className extends $className {}
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
    fun `java android component using field injection without @AndroidEntryPoint triggers error`(
        @TestParameter(
            value =
                [
                    "android.app.Activity",
                    "android.app.Fragment",
                    "android.app.Service",
                    "android.content.ContentProvider",
                    "android.content.BroadcastReceiver",
                    "androidx.fragment.app.Fragment"
                ]
        )
        androidEntryPoint: String
    ) {
        val classPackage = androidEntryPoint.substringBeforeLast(".")
        val className = androidEntryPoint.substringAfterLast(".")

        fun expectedErrorMessage(): String {

            val errorHighlight = "AndroidX${className}".map { "~" }.joinToString(separator = "")

            return """
                src/androidx/AndroidX$className.java:7: Error: This class is missing the @${ANDROID_ENTRY_POINT.substringAfterLast(".")} [MissingHiltAnnotation]
                class AndroidX$className extends $className {
                      $errorHighlight
                1 errors, 0 warnings
            """
        }

        fun expectedFixDiff(): String =
            """
            Fix for src/androidx/AndroidX$className.java line 7: Add AndroidEntryPoint annotation:
            @@ -7 +7
            - class AndroidX$className extends $className {
            + class @dagger.hilt.android.AndroidEntryPoint
            + AndroidX$className extends $className {
        """
                .trimIndent()

        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                injectAnnotation,
                TestFiles.java(
                        """
                package $classPackage;

                class $className {}

                """
                    )
                    .indented(),
                TestFiles.java(
                        """
                package androidx;

                import $androidEntryPoint;
                import javax.inject.Inject;


                class AndroidX$className extends $className {
                    @Inject
                    String myString;
                }
                    """
                    )
                    .indented(),
            )
            .issues(MissingHiltAnnotationDetector.ISSUE)
            .run()
            .expect(expectedErrorMessage())
            .expectErrorCount(1)
            .expectFixDiffs(expectedFixDiff())
    }

    @Test
    fun `kotlin android component using field injection with @AndroidEntryPoint does not trigger error`(
        @TestParameter(
            value =
                [
                    "android.app.Activity",
                    "android.app.Fragment",
                    "android.app.Service",
                    "android.content.ContentProvider",
                    "android.content.BroadcastReceiver",
                    "androidx.fragment.app.Fragment"
                ]
        )
        androidEntryPoint: String
    ) {
        val classPackage = androidEntryPoint.substringBeforeLast(".")
        val className = androidEntryPoint.substringAfterLast(".")

        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                        """
                package $classPackage

                class $className

                """
                    )
                    .indented(),
                TestFiles.kotlin(
                        """
                package androidx

                import $androidEntryPoint
                import $ANDROID_ENTRY_POINT

                @${ANDROID_ENTRY_POINT.substringAfterLast(".")}
                class AndroidX$className: $className
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
    fun `kotlin android component using field injection without @AndroidEntryPoint triggers error`(
        @TestParameter(
            value =
                [
                    "android.app.Activity",
                    "android.app.Fragment",
                    "android.app.Service",
                    "android.content.ContentProvider",
                    "android.content.BroadcastReceiver",
                    "androidx.fragment.app.Fragment"
                ]
        )
        androidEntryPoint: String
    ) {
        val classPackage = androidEntryPoint.substringBeforeLast(".")
        val className = androidEntryPoint.substringAfterLast(".")

        fun expectedErrorMessage(): String {

            val errorHighlight = "AndroidX$className".map { "~" }.joinToString(separator = "")

            return """
                src/androidx/AndroidX$className.kt:6: Error: This class is missing the @${ANDROID_ENTRY_POINT.substringAfterLast(".")} [MissingHiltAnnotation]
                class AndroidX$className : $className {
                      $errorHighlight
                1 errors, 0 warnings
            """
        }

        fun expectedFixDiff(): String =
            """
                Fix for src/androidx/AndroidX${className}.kt line 6: Add AndroidEntryPoint annotation:
                @@ -6 +6
                - class AndroidX${className} : $className {
                + class @dagger.hilt.android.AndroidEntryPoint
                + AndroidX${className} : $className {
            """
                .trimIndent()

        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                injectAnnotation,
                TestFiles.kotlin(
                        """
                package $classPackage

                class $className

                """
                    )
                    .indented(),
                TestFiles.kotlin(
                        """
                package androidx

                import javax.inject.Inject
                import $androidEntryPoint

                class AndroidX${className} : ${className} {
                    @Inject lateinit var something: String
                }
                    """
                    )
                    .indented(),
            )
            .issues(MissingHiltAnnotationDetector.ISSUE)
            .run()
            .expect(expectedErrorMessage())
            .expectErrorCount(1)
            .expectFixDiffs(expectedFixDiff())
    }

    @Test
    fun `kotlin android component not using field injection without @AndroidEntryPoint does not trigger error`(
        @TestParameter(
            value =
                [
                    "android.app.Activity",
                    "android.app.Fragment",
                    "android.app.Service",
                    "android.content.ContentProvider",
                    "android.content.BroadcastReceiver",
                    "androidx.fragment.app.Fragment"
                ]
        )
        androidEntryPoint: String
    ) {
        val classPackage = androidEntryPoint.substringBeforeLast(".")
        val className = androidEntryPoint.substringAfterLast(".")

        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                        """
                package $classPackage

                class $className

                """
                    )
                    .indented(),
                TestFiles.kotlin(
                        """
                package androidx

                class AndroidX$className : $className
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
    fun `java android component not using field injection without @AndroidEntryPoint does not trigger error`(
        @TestParameter(
            value =
                [
                    "android.app.Activity",
                    "android.app.Fragment",
                    "android.app.Service",
                    "android.content.ContentProvider",
                    "android.content.BroadcastReceiver",
                    "androidx.fragment.app.Fragment"
                ]
        )
        androidEntryPoint: String
    ) {
        val classPackage = androidEntryPoint.substringBeforeLast(".")
        val className = androidEntryPoint.substringAfterLast(".")

        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.java(
                        """
                package $classPackage;

                class $className {}

                """
                    )
                    .indented(),
                TestFiles.java(
                        """
                package androidx;

                class AndroidX$className extends $className {}
                    """
                    )
                    .indented(),
            )
            .issues(MissingHiltAnnotationDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }
}
