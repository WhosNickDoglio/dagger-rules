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
import com.android.tools.lint.detector.api.isKotlin
import dev.whosnickdoglio.lint.shared.BINDS
import dev.whosnickdoglio.lint.shared.MODULE
import dev.whosnickdoglio.lint.shared.MULTIBINDS
import dev.whosnickdoglio.lint.shared.PROVIDES
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

internal class MissingModuleAnnotationDetector : Detector(), SourceCodeScanner {
    private val daggerAnnotations = listOf(BINDS, PROVIDES, MULTIBINDS)

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
            override fun visitClass(node: UClass) {
                if (!node.hasAnnotation(MODULE)) {
                    if (isKotlin(node.lang) && context.evaluator.isCompanion(node)) {
                        // Early out, other methods should already trigger lint warning?
                        return
                    }

                    val needsModuleAnnotation =
                        node.methods.any { method ->
                            daggerAnnotations.any { annotation -> method.hasAnnotation(annotation) }
                        }

                    if (needsModuleAnnotation) {
                        context.report(
                            Incident(
                                issue = ISSUE,
                                scope = node,
                                location = context.getNameLocation(node),
                                message = ISSUE.getExplanation(TextFormat.RAW),
                                fix =
                                fix()
                                    .name("Add @Module annotation")
                                    .annotate(MODULE, context, node)
                                    .autoFix(robot = true, independent = true)
                                    .build(),
                            ),
                        )
                    }
                }
            }
        }
    }

    companion object {
        private val implementation =
            Implementation(MissingModuleAnnotationDetector::class.java, Scope.JAVA_FILE_SCOPE)

        internal val ISSUE =
            Issue.create(
                id = "MissingModuleAnnotation",
                briefDescription = "Missing `@Module` annotation",
                explanation =
                """
                    Provides or binds methods won't be picked up if the class isn't annotated with @Module.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#classes-with-provides-binds-or-multibinds-methods-should-be-annotated-with-module for more information.
                    """,
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.ERROR,
                implementation = implementation,
            )
    }
}
