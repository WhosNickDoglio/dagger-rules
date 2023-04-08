/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.dagger.detectors

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.android.tools.lint.detector.api.TextFormat
import dev.whosnickdoglio.lint.shared.PROVIDES
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.util.isConstructorCall

internal class ConstructorInjectionOverProvidesDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitAnnotation(node: UAnnotation) {
                if (node.qualifiedName == PROVIDES) {
                    val method = node.uastParent as? UCallExpression ?: return
                    if (method.isConstructorCall()) {
                        context.report(
                            issue = ISSUE,
                            location = context.getLocation(method),
                            message = ISSUE.getExplanation(TextFormat.TEXT)
                        )
                    }
                }
            }
        }

    companion object {

        private val implementation =
            Implementation(
                ConstructorInjectionOverProvidesDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        val ISSUE =
            Issue.create(
                    id = "ConstructorInjectionOverProvidesMethods",
                    briefDescription = "@Provides method used instead of constructor injection",
                    explanation =
                        """
                    `@Provides` methods are great for adding third party libraries or classes that require Builders or Factories \
                    to the Dagger graph but for classes with simple constructors you should just add a `@Inject` annotation to the constructor
                    """,
                    category = Category.CORRECTNESS,
                    priority = 5,
                    severity = Severity.WARNING,
                    implementation = implementation
                )
                .setEnabledByDefault(false)
    }
}
