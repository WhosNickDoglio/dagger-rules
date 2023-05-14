/*
 * Copyright (C) 2023 Nicholas Doglio
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
import dev.whosnickdoglio.anvil.CONTRIBUTES_TO
import dev.whosnickdoglio.lint.shared.MODULE
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

// TODO make this configurable for Anvil scopes in quick fix
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

                    if (!element.hasAnnotation(CONTRIBUTES_TO)) {
                        context.report(
                            Incident(context, ISSUE)
                                .location(context.getNameLocation(element))
                                .message(ISSUE.getExplanation(TextFormat.RAW))
                                .fix(
                                    fix()
                                        .name("Add @ContributesTo annotation")
                                        .annotate(CONTRIBUTES_TO)
                                        .range(context.getNameLocation(element))
                                        .build()
                                )
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
                    """
                    This Dagger module is missing a `@ContributesTo` annotation for Anvil to pick it up. See https://whosnickdoglio.dev/dagger-rules/rules/#a-class-annotated-with-module-should-also-be-annotated-with-contributesto for more information.
                    """,
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.ERROR,
                implementation = implementation
            )
    }
}
