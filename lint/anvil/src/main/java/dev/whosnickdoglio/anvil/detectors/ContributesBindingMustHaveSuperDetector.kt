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
import dev.whosnickdoglio.anvil.CONTRIBUTES_BINDING
import dev.whosnickdoglio.anvil.CONTRIBUTES_MULTI_BINDING
import dev.whosnickdoglio.anvil.CONTRIBUTES_TO
import dev.whosnickdoglio.lint.shared.MODULE
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

internal class ContributesBindingMustHaveSuperDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        // Anvil is Kotlin only
        if (!isKotlin(context.uastFile?.lang)) return null
        return object : UElementHandler() {
            override fun visitAnnotation(node: UAnnotation) {
                if (
                    node.qualifiedName == CONTRIBUTES_BINDING ||
                        node.qualifiedName == CONTRIBUTES_MULTI_BINDING
                ) {
                    val clazz = node.uastParent as? UClass ?: return

                    // Account for Any supertype
                    // TODO handle `boundType = Any`
                    if (clazz.supers.size == 1) {
                        if (clazz.hasAnnotation(MODULE)) {
                            context.report(
                                Incident(context, ISSUE_CONTRIBUTES_TO_INSTEAD_OF_BINDING)
                                    .location(context.getNameLocation(clazz))
                                    .message(
                                        ISSUE_CONTRIBUTES_TO_INSTEAD_OF_BINDING.getExplanation(
                                            TextFormat.RAW
                                        )
                                    )
                                    .fix(
                                        // TODO remove `ContributesBinding` annotation
                                        fix()
                                            .name("Did you mean `@ContributesTo` annotation?")
                                            .annotate(CONTRIBUTES_TO)
                                            .range(context.getNameLocation(clazz))
                                            .build()
                                    )
                            )
                        }

                        context.report(
                            Incident(context, ISSUE_BINDING_NO_SUPER)
                                .location(context.getNameLocation(clazz))
                                .message(ISSUE_BINDING_NO_SUPER.getExplanation(TextFormat.RAW))
                                .fix(null)
                        )
                    }
                }
            }
        }
    }

    companion object {

        private val implementation =
            Implementation(
                ContributesBindingMustHaveSuperDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )

        internal val ISSUE_BINDING_NO_SUPER =
            Issue.create(
                id = "ContributesBindingMustHaveSuper",
                briefDescription = "Classes annotated with ContributesBinding need a super",
                explanation = "Hello friend",
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.WARNING,
                implementation = implementation
            )

        internal val ISSUE_CONTRIBUTES_TO_INSTEAD_OF_BINDING =
            Issue.create(
                id = "UseContributesToInstead",
                briefDescription = "Hello friend",
                explanation = "Hello friend",
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.ERROR,
                implementation = implementation
            )
    }
}
