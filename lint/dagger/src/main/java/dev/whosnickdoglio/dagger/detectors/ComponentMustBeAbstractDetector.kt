/*
 * Copyright (C) 2024 Nicholas Doglio
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
import dev.whosnickdoglio.lint.annotations.anvil.CONTRIBUTES_SUBCOMPONENT
import dev.whosnickdoglio.lint.annotations.anvil.MERGE_COMPONENT
import dev.whosnickdoglio.lint.annotations.anvil.MERGE_SUBCOMPONENT
import dev.whosnickdoglio.lint.annotations.dagger.COMPONENT
import dev.whosnickdoglio.lint.annotations.dagger.SUBCOMPONENT
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

internal class ComponentMustBeAbstractDetector : Detector(), SourceCodeScanner {
    private val oldClassPattern =
        "(object|enum\\s+class|annotation\\s+class|sealed\\s+class|data\\s+class|enum|class)"
            .toRegex()

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitAnnotation(node: UAnnotation) {
                if (node.qualifiedName in componentAnnotations) {
                    val component = node.uastParent as? UClass ?: return

                    if (!context.evaluator.isAbstract(component)) {
                        context.report(
                            Incident(
                                issue = ISSUE,
                                scope = component,
                                location = context.getLocation(element = component),
                                message = ISSUE.getExplanation(TextFormat.TEXT),
                                fix =
                                    fix()
                                        .replace()
                                        .name("Make ${component.name} an interface")
                                        .pattern(oldClassPattern.toString())
                                        .with("interface")
                                        .build(),
                            )
                        )
                    }
                }
            }
        }

    companion object {
        private val implementation =
            Implementation(ComponentMustBeAbstractDetector::class.java, Scope.JAVA_FILE_SCOPE)

        internal val componentAnnotations =
            setOf(
                // Vanilla Dagger
                COMPONENT,
                SUBCOMPONENT,
                // Anvil
                MERGE_COMPONENT,
                MERGE_SUBCOMPONENT,
                CONTRIBUTES_SUBCOMPONENT,
            )

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
