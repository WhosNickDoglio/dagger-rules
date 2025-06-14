/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.hilt.detectors

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Incident
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.android.tools.lint.detector.api.TextFormat
import com.intellij.psi.PsiClass
import dev.whosnickdoglio.lint.annotations.dagger.INJECT
import dev.whosnickdoglio.lint.annotations.hilt.HILT_VIEW_MODEL
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

/**
 * A Lint [Detector] that checks Java and Kotlin files for missing Hilt annotations on specific
 * Android components. For Hilt to work as expected the `@AndroidEntryPoint`, `@HiltViewModel` and
 * `@HiltAndroidApp`.
 */
internal class MissingHiltViewModelAnnotationDetector : Detector(), SourceCodeScanner {
    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitClass(node: UClass) {
                val clazz = node.javaPsi
                val isViewModelSubclass =
                    context.evaluator.extendsClass(clazz, HILT_VIEW_MODEL_PACKAGE, true)

                if (
                    isViewModelSubclass &&
                        clazz.hasInjectedConstructor() &&
                        !clazz.hasAnnotation(HILT_VIEW_MODEL)
                ) {
                    context.report(
                        Incident(context, ISSUE_MISSING_ANNOTATION)
                            .location(context.getNameLocation(clazz))
                            .message(ISSUE_MISSING_ANNOTATION.getExplanation(TextFormat.RAW))
                            .fix(
                                fix()
                                    .name(
                                        "Add ${HILT_VIEW_MODEL.substringAfterLast(".")} annotation"
                                    )
                                    .annotate(HILT_VIEW_MODEL, context, clazz)
                                    .range(context.getLocation(node.sourcePsi))
                                    .build()
                            )
                    )
                } else if (
                    isViewModelSubclass &&
                        !clazz.hasInjectedConstructor() &&
                        clazz.hasAnnotation(HILT_VIEW_MODEL)
                ) {
                    context.report(
                        Incident(context, ISSUE_UNNECESSARY_ANNOTATION)
                            .location(context.getNameLocation(clazz))
                            .message(
                                "This class is missing the `@${HILT_VIEW_MODEL.substringAfterLast(".")}`"
                            )
                            .fix(null)
                    )
                }
            }
        }

    private fun PsiClass.hasInjectedConstructor(): Boolean =
        constructors.any { method -> method.hasAnnotation(INJECT) }

    companion object {
        private const val HILT_VIEW_MODEL_PACKAGE = "androidx.lifecycle.ViewModel"

        private val implementation =
            Implementation(
                MissingHiltViewModelAnnotationDetector::class.java,
                Scope.JAVA_FILE_SCOPE,
            )

        internal val ISSUE_MISSING_ANNOTATION =
            Issue.create(
                id = "MissingHiltViewModelAnnotation",
                briefDescription = "Android Component is missing Hilt annotation",
                explanation =
                    """
                    ViewModels using Hilt need to be annotated with `@HiltViewModel`.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#viewmodel-subclasses-should-be-annotated-with-hiltviewmodel for more information.
                    """,
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.ERROR,
                implementation = implementation,
            )

        internal val ISSUE_UNNECESSARY_ANNOTATION =
            Issue.create(
                id = "UnnecessaryHiltViewModelAnnotation",
                briefDescription = "ViewModel doesn't need @HiltViewModel",
                explanation = "Hello friend",
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.WARNING,
                implementation = implementation,
            )
    }
}
