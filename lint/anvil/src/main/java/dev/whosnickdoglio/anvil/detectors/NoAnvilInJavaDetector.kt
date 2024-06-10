/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.anvil.detectors

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
import com.android.tools.lint.detector.api.isKotlin
import dev.whosnickdoglio.anvil.CONTRIBUTES_BINDING
import dev.whosnickdoglio.anvil.CONTRIBUTES_MULTI_BINDING
import dev.whosnickdoglio.anvil.CONTRIBUTES_SUBCOMPONENT
import dev.whosnickdoglio.anvil.CONTRIBUTES_SUBCOMPONENT_FACTORY
import dev.whosnickdoglio.anvil.CONTRIBUTES_TO
import dev.whosnickdoglio.anvil.MERGE_COMPONENT
import dev.whosnickdoglio.anvil.MERGE_SUBCOMPONENT
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UElement

internal class NoAnvilInJavaDetector :
    Detector(),
    SourceCodeScanner {
    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        if (isKotlin(context.uastFile?.lang)) return null
        return object : UElementHandler() {
            override fun visitAnnotation(node: UAnnotation) {
                if (node.qualifiedName in anvilAnnotations) {
                    context.report(
                        Incident(context, ISSUE)
                            .location(context.getLocation(node))
                            .message(ISSUE.getExplanation(TextFormat.RAW)),
                    )
                }
            }
        }
    }

    companion object {
        internal val anvilAnnotations =
            setOf(
                CONTRIBUTES_TO,
                CONTRIBUTES_BINDING,
                CONTRIBUTES_MULTI_BINDING,
                CONTRIBUTES_SUBCOMPONENT,
                CONTRIBUTES_SUBCOMPONENT_FACTORY,
                MERGE_COMPONENT,
                MERGE_SUBCOMPONENT,
            )
        private val implementation =
            Implementation(NoAnvilInJavaDetector::class.java, Scope.JAVA_FILE_SCOPE)

        internal val ISSUE =
            Issue.create(
                id = "NoAnvilJavaUsage",
                briefDescription = "Anvil doesn't support Java",
                explanation =
                """
                        Anvil works as a Kotlin compiler plugin and does not support being used from Java. \
                        You can convert this class to Kotlin so it can use Anvil annotations.

                        See https://whosnickdoglio.dev/dagger-rules/rules/#anvil-cannot-be-used-from-java for more information.
                    """,
                category = Category.CORRECTNESS,
                priority = 10,
                severity = Severity.ERROR,
                implementation = implementation,
            )
    }
}
