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
import com.android.tools.lint.detector.api.isKotlin
import dev.whosnickdoglio.anvil.CONTRIBUTES_BINDING
import dev.whosnickdoglio.anvil.CONTRIBUTES_MULTI_BINDING
import dev.whosnickdoglio.lint.shared.INJECT
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

/**
 * A Kotlin only [Detector] for the Anvil library that suggests using the provided
 * `@ContributesBinding` or `@ContributesMultibinding` annotations for classes that use Dagger and
 * implement an interface or abstract class.
 */
internal class MissingContributesBindingDetector : Detector(), SourceCodeScanner {
    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        // Anvil is Kotlin only
        if (!isKotlin(context.uastFile?.lang)) return null
        return object : UElementHandler() {
            override fun visitClass(node: UClass) {
                val hasBindingAnnotations =
                    node.hasAnnotation(CONTRIBUTES_BINDING) ||
                        node.hasAnnotation(CONTRIBUTES_MULTI_BINDING)

                if (
                    node.constructors.any { method -> method.hasAnnotation(INJECT) } &&
                    // TODO this feels naive
                    // Ignore Any
                    node.superTypes.size > 1 &&
                    !hasBindingAnnotations
                ) {
                    val hasNoGenerics = node.superTypes.filter { !it.hasParameters() }

                    // Ignore Any
                    if (hasNoGenerics.size > 1) {
                        context.report(
                            Incident(context, ISSUE)
                                .location(context.getNameLocation(node))
                                .message(
                                    "Contribute this binding to the Dagger graph using an Anvil annotation",
                                )
                                .fix(
                                    fix()
                                        // TODO try and give better fixes
                                        .alternatives(
                                            fix()
                                                .name("Add @ContributesBinding annotation")
                                                .annotate(CONTRIBUTES_BINDING, context, node)
                                                .autoFix(robot = true, independent = true)
                                                .build(),
                                            fix()
                                                .name("Add @ContributesMultibinding annotation")
                                                .annotate(CONTRIBUTES_MULTI_BINDING, context, node)
                                                .autoFix(robot = true, independent = true)
                                                .build(),
                                        ),
                                ),
                        )
                    }
                }
            }
        }
    }

    companion object {
        private val implementation =
            Implementation(MissingContributesBindingDetector::class.java, Scope.JAVA_FILE_SCOPE)

        internal val ISSUE =
            Issue.create(
                id = "MissingContributesBindingAnnotation",
                briefDescription = "Hello friend",
                explanation = "Hello friend",
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.WARNING,
                implementation = implementation,
            )
    }
}
