/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.hilt.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import dev.whosnickdoglio.hilt.HILT_VIEW_MODEL
import dev.whosnickdoglio.stubs.injectAnnotation
import org.junit.Test

class MissingHiltViewModelAnnotationDetectorTest {

    private val viewModelStub =
        TestFiles.kotlin(
                """
                        package androidx.lifecycle
                        class ViewModel
                    """
            )
            .indented()

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
            .issues(MissingHiltViewModelAnnotationDetector.ISSUE_MISSING_ANNOTATION)
            .run()
            .expect(
                """
                    src/MyViewModel.kt:4: Error: This class is missing the @HiltViewModel [MissingHiltViewModelAnnotation]
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
            .issues(MissingHiltViewModelAnnotationDetector.ISSUE_MISSING_ANNOTATION)
            .run()
            .expect(
                """
                src/MyViewModel.java:4: Error: This class is missing the @HiltViewModel [MissingHiltViewModelAnnotation]
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
            .issues(MissingHiltViewModelAnnotationDetector.ISSUE_MISSING_ANNOTATION)
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
            .issues(MissingHiltViewModelAnnotationDetector.ISSUE_MISSING_ANNOTATION)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin ViewModel with @HiltViewModel does not triggers error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                injectAnnotation,
                viewModelStub,
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
            .issues(MissingHiltViewModelAnnotationDetector.ISSUE_MISSING_ANNOTATION)
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
            .issues(MissingHiltViewModelAnnotationDetector.ISSUE_MISSING_ANNOTATION)
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
            .issues(MissingHiltViewModelAnnotationDetector.ISSUE_MISSING_ANNOTATION)
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
            .issues(MissingHiltViewModelAnnotationDetector.ISSUE_MISSING_ANNOTATION)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }
}
