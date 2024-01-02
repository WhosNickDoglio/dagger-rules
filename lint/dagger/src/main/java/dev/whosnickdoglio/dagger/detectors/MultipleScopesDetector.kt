/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.dagger.detectors

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Incident
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Location
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.android.tools.lint.detector.api.TextFormat
import dev.whosnickdoglio.lint.shared.BINDS
import dev.whosnickdoglio.lint.shared.PROVIDES
import dev.whosnickdoglio.lint.shared.SCOPE
import org.jetbrains.uast.UAnnotated
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.resolveToUElement

/**
 * A Kotlin and Java [Detector] for Dagger that warns if there is an attempt to add an object to the
 * DI graph with multiple scopes.
 */
internal class MultipleScopesDetector : Detector(), SourceCodeScanner {
    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UClass::class.java, UMethod::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitClass(node: UClass) {
                node.report(context, context.getNameLocation(node))
            }

            override fun visitMethod(node: UMethod) {
                if (node.hasAnnotation(BINDS) || node.hasAnnotation(PROVIDES)) {
                    node.report(context, context.getNameLocation(node))
                }
            }
        }

    private fun UAnnotated.report(context: JavaContext, location: Location) {
        val scopeAnnotations =
            uAnnotations
                .map { annotation -> annotation.resolveToUElement() }
                .filterIsInstance<UAnnotated>()
                .filter { annotated ->
                    annotated.uAnnotations.any { annotation -> annotation.qualifiedName == SCOPE }
                }

        if (scopeAnnotations.size > 1) {
            context.report(
                incident =
                    Incident(context)
                        .location(location)
                        .message(ISSUE.getExplanation(TextFormat.RAW))
            )
        }
    }

    companion object {

        private val implementation =
            Implementation(MultipleScopesDetector::class.java, Scope.JAVA_FILE_SCOPE)

        internal val ISSUE =
            Issue.create(
                id = "MultipleScopes",
                briefDescription = "An object cannot declare more than one `@Scope`",
                explanation =
                    "Objects on the DI graph can only have one `@Scope` annotation, please remove one",
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.ERROR,
                implementation = implementation
            )
    }
}
