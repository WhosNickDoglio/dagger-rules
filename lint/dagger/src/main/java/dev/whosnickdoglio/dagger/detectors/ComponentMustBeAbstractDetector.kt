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
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.android.tools.lint.detector.api.TextFormat
import dev.whosnickdoglio.lint.shared.COMPONENT
import dev.whosnickdoglio.lint.shared.SUBCOMPONENT
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

internal class ComponentMustBeAbstractDetector : Detector(), SourceCodeScanner {
    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitAnnotation(node: UAnnotation) {
                if (node.qualifiedName == COMPONENT || node.qualifiedName == SUBCOMPONENT) {
                    val component = node.uastParent as? UClass ?: return

                    if (!context.evaluator.isAbstract(component)) {
                        context.report(
                            Incident(
                                issue = ISSUE,
                                scope = component,
                                location = context.getNameLocation(component),
                                message = ISSUE.getExplanation(TextFormat.TEXT),
                                fix = null,
                                // TODO
                            ),
                        )
                    }
                }
            }
        }

    companion object {
        private val implementation =
            Implementation(ComponentMustBeAbstractDetector::class.java, Scope.JAVA_FILE_SCOPE)

        internal val ISSUE =
            Issue.create(
                id = "ComponentMustBeAbstract",
                briefDescription = "A Dagger `Component` must be an interface or abstract",
                explanation =
                """
                    A type annotated with @Component or @Subcomponent need to be abstract.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#classes-annotated-with-component-must-be-abstract for more information.
                """,
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.ERROR,
                implementation = implementation,
            )
    }
}
