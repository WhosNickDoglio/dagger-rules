/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.dagger.detectors

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
import dev.whosnickdoglio.lint.shared.INJECT
import dev.whosnickdoglio.lint.shared.SCOPE
import org.jetbrains.uast.UAnnotated
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.resolveToUElement

/**
 * A Lint rule that warns if a class is annotated with any scope annotation but does not have a
 * `@Inject` annotation on any constructor that it will not be added to the Dagger graph.
 */
internal class ScopedWithoutInjectAnnotationDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitClass(node: UClass) {
                if (!context.evaluator.isAbstract(node)) {
                    val sourceScopeAnnotations =
                        node.uAnnotations
                            .map { annotation -> annotation.resolveToUElement() }
                            .filterIsInstance<UAnnotated>()
                            .filter { annotated ->
                                annotated.uAnnotations.any { annotation ->
                                    annotation.qualifiedName == SCOPE
                                }
                            }
                            .filterIsInstance<UClass>()

                    val scopeAnnotations =
                        node.uAnnotations.filter { annotation ->
                            sourceScopeAnnotations.any { scope ->
                                scope.qualifiedName == annotation.qualifiedName
                            }
                        }

                    val isInjected =
                        node.constructors.any { constructor -> constructor.hasAnnotation(INJECT) }

                    if (scopeAnnotations.isNotEmpty() && !isInjected) {
                        scopeAnnotations.forEach { annotation ->
                            context.report(
                                issue = ISSUE,
                                location = context.getLocation(annotation),
                                message = ISSUE.getExplanation(TextFormat.TEXT),
                                quickfixData =
                                    fix()
                                        .name("Remove unnecessary scope annotation")
                                        .replace()
                                        .text(annotation.asRenderString())
                                        .with("")
                                        .build(),
                            )
                        }
                    }
                }
            }
        }

    companion object {
        private val implementation =
            Implementation(ScopedWithoutInjectAnnotationDetector::class.java, Scope.JAVA_FILE_SCOPE)

        internal val ISSUE =
            Issue.create(
                id = "ScopedWithoutInjection",
                briefDescription = "Class is scoped without using the `@Inject` annotation",
                explanation =
                    "Without the `@Inject` annotation this class is not added to the " +
                        "DI graph which means the scope annotation doesn't do anything.",
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.ERROR,
                implementation = implementation
            )
    }
}
