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
import com.android.tools.lint.detector.api.LocationType
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.android.tools.lint.detector.api.TextFormat
import dev.whosnickdoglio.lint.annotations.hilt.ENTRY_POINT
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

internal class EntryPointMustBeAnInterfaceDetector : Detector(), SourceCodeScanner {
    private val oldClassPattern =
        ("(object|abstract\\s+class|enum\\s+class|annotation\\s+class|" +
                "sealed\\s+class|data\\s+class|enum|class)")
            .toRegex()

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitAnnotation(node: UAnnotation) {
                if (node.qualifiedName == ENTRY_POINT) {
                    val entryPoint = node.uastParent as? UClass ?: return

                    if (!entryPoint.isInterface || entryPoint.isAnnotationType) {
                        context.report(
                            Incident(context, ISSUE)
                                .location(context.getLocation(entryPoint, type = LocationType.ALL))
                                .message(ISSUE.getExplanation(TextFormat.RAW))
                                .fix(
                                    fix()
                                        .replace()
                                        .name("Make ${entryPoint.javaPsi.name} an interface")
                                        .pattern(oldClassPattern.toString())
                                        .with("interface")
                                        .build()
                                )
                        )
                    }
                }
            }
        }

    companion object {
        private val implementation =
            Implementation(EntryPointMustBeAnInterfaceDetector::class.java, Scope.JAVA_FILE_SCOPE)

        internal val ISSUE =
            Issue.create(
                id = "EntryPointMustBeAnInterface",
                briefDescription = "Hilt entry points must be interfaces",
                explanation =
                    """
                    The `@EntryPoint` annotation can only be applied to `interfaces`, trying to apply it to anything else will cause an error at compile time.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#the-entrypoint-annotation-can-only-be-applied-to-interfaces for more information.
                    """,
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.ERROR,
                implementation = implementation,
            )
    }
}
