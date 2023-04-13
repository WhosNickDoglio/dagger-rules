/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.anvil.detectors

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
import com.android.tools.lint.detector.api.isKotlin
import dev.whosnickdoglio.anvil.CONTRIBUTES_TO
import dev.whosnickdoglio.lint.shared.MODULE
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

internal class MissingContributesToDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        // Anvil is Kotlin only
        if (!isKotlin(context.uastFile?.lang)) return null
        return object : UElementHandler() {
            override fun visitAnnotation(node: UAnnotation) {
                if (node.qualifiedName == MODULE) {
                    val element = node.uastParent as? UClass ?: return

                    val hasContributesToAnnotation =
                        element.uAnnotations.any { annotation ->
                            annotation.qualifiedName == CONTRIBUTES_TO
                        }

                    if (!hasContributesToAnnotation) {
                        context.report(
                            issue = ISSUE,
                            location = context.getNameLocation(element),
                            message = ISSUE.getExplanation(TextFormat.TEXT),
                            quickfixData =
                                fix()
                                    .name("Add @ContributesTo annotation")
                                    .annotate(CONTRIBUTES_TO)
                                    .range(context.getNameLocation(element))
                                    .build()
                        )
                    }
                }
            }
        }
    }

    companion object {
        private val implementation =
            Implementation(MissingContributesToDetector::class.java, Scope.JAVA_FILE_SCOPE)

        internal val ISSUE =
            Issue.create(
                id = "MissingContributesToAnnotation",
                briefDescription = "Module missing @ContributesTo annotation",
                explanation =
                    "This Dagger module is missing a `@ContributesTo` annotation for Anvil to pick it up",
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.ERROR,
                implementation = implementation
            )
    }
}
