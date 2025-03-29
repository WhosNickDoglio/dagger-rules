/*
 * Copyright (C) 2025 Nicholas Doglio
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
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.android.tools.lint.detector.api.TextFormat
import com.android.tools.lint.detector.api.isJava
import com.android.tools.lint.detector.api.isKotlin
import dev.whosnickdoglio.lint.annotations.dagger.PROVIDES
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.getContainingUClass

internal class StaticProvidesDetector : Detector(), SourceCodeScanner {
    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitAnnotation(node: UAnnotation) {
                if (node.qualifiedName == PROVIDES) {
                    val method = node.uastParent as? UMethod ?: return
                    when {
                        isJava(method.language) -> javaCheck(context, method)
                        isKotlin(method.language) -> kotlinCheck(context, method)
                    }
                }
            }
        }

    private fun javaCheck(context: JavaContext, method: UMethod) {
        if (!context.evaluator.isStatic(method)) {
            context.report(
                Incident(
                    issue = ISSUE,
                    scope = method,
                    location = context.getNameLocation(method),
                    message = ISSUE.getExplanation(TextFormat.TEXT),
                )
            )
        }
    }

    private fun kotlinCheck(context: JavaContext, method: UMethod) {
        val containingClass = method.getContainingUClass()
        val sourcePsi = containingClass?.sourcePsi ?: return
        if (sourcePsi !is KtObjectDeclaration) {
            context.report(
                Incident(
                    issue = ISSUE,
                    scope = method,
                    location = context.getLocation(method),
                    message = ISSUE.getExplanation(TextFormat.TEXT),
                )
            )
        }
    }

    companion object {
        private val implementation =
            Implementation(StaticProvidesDetector::class.java, Scope.JAVA_FILE_SCOPE)

        internal val ISSUE =
            Issue.create(
                id = "StaticProvides",
                briefDescription = "Not using static @Provides methods",
                explanation =
                    """
                    @Provides methods should be static.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#provides-methods-should-be-static for more information.
                """,
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.WARNING,
                implementation = implementation,
            )
    }
}
