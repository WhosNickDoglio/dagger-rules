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
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

/**
 * A Kotlin only [Detector] for the Anvil library that warns against possible misuse of the
 * `@ContributesBinding` and `@ContributesMultibinding` annotations. The two primary warnings
 * include suggesting the `@ContributesTo` annotation instead of either binding annotation when
 * working with a Dagger module as well as preventing the use of binding annotations with a class
 * with no super type.
 */
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
                    val annotation = node.sourcePsi as? KtAnnotationEntry ?: return

                    // Accounts for Any supertype
                    if (clazz.supers.size == 1) {
                        checkIsDaggerModule(clazz, context, node)

                        val annotationArguments =
                            annotation.valueArguments.map { valueArgument ->
                                valueArgument.getArgumentName()?.asName?.asString() to
                                    valueArgument.getArgumentExpression()?.text
                            }
                        if (!annotationArguments.contains(BOUND_TYPE to ANY_CLASS)) {
                            context.report(
                                Incident(context, ISSUE_BINDING_NO_SUPER)
                                    .location(context.getNameLocation(clazz))
                                    .message(ISSUE_BINDING_NO_SUPER.getExplanation(TextFormat.RAW))
                                    .fix(null),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun checkIsDaggerModule(
        clazz: UClass,
        context: JavaContext,
        node: UAnnotation,
    ) {
        if (clazz.hasAnnotation(MODULE)) {
            context.report(
                Incident(context, ISSUE_CONTRIBUTES_TO_INSTEAD_OF_BINDING)
                    .location(context.getLocation(node))
                    .message(ISSUE_CONTRIBUTES_TO_INSTEAD_OF_BINDING.getExplanation(TextFormat.RAW))
                    .fix(
                        fix()
                            .name("Did you mean to use the `@ContributesTo` annotation?")
                            .composite(
                                fix()
                                    .replace()
                                    .pattern(
                                        "(?i)(.*${node.qualifiedName?.substringAfterLast(".")})",
                                    )
                                    .with("")
                                    .build(),
                                fix()
                                    .annotate(CONTRIBUTES_TO, context, clazz)
                                    .autoFix(robot = true, independent = true)
                                    .build(),
                            ),
                    ),
            )
        }
    }

    companion object {
        private const val BOUND_TYPE = "boundType"
        private const val ANY_CLASS = "Any::class"

        private val implementation =
            Implementation(
                ContributesBindingMustHaveSuperDetector::class.java,
                Scope.JAVA_FILE_SCOPE,
            )

        internal val ISSUE_BINDING_NO_SUPER =
            Issue.create(
                id = "ContributesBindingMustHaveSuper",
                briefDescription = "Classes annotated with ContributesBinding need a super",
                explanation =
                "The `ContributesBinding` annotation is used to bind concrete implementations to " +
                    "an interface/abstract they implement if there is no interface or abstract class to " +
                    "implement using `@ContributesBinding` will throw an error at compile time. ",
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.WARNING,
                implementation = implementation,
            )

        internal val ISSUE_CONTRIBUTES_TO_INSTEAD_OF_BINDING =
            Issue.create(
                id = "UseContributesToInstead",
                briefDescription = "Use ContributesTo for Dagger modules",
                explanation =
                "The `ContributesTo` annotation is used to contribute Dagger modules to the DI " +
                    "graph whereas the `ContributesBinding` annotation is used to bind specific classes to " +
                    "one of their super interfaces/abstract classes in the DI graph and would not work " +
                    "with a Dagger module.",
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.ERROR,
                implementation = implementation,
            )
    }
}
