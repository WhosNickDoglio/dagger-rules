/*
 * Copyright (C) 2024 Nicholas Doglio
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
import dev.whosnickdoglio.lint.annotations.dagger.ASSISTED_INJECT
import dev.whosnickdoglio.lint.annotations.dagger.SCOPE
import org.jetbrains.uast.UAnnotated
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.resolveToUElement

/**
 * A Lint rule that warns if a class is annotated with any scope annotation but does not have a
 * `@Inject` annotation on any constructor that it will not be added to the Dagger graph.
 */
internal class ScopedAssistedInjectedDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitClass(node: UClass) {
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

                val scopeAnnotationsOnCurrentClass =
                    node.uAnnotations.filter { annotation ->
                        sourceScopeAnnotations.any { scope ->
                            scope.qualifiedName == annotation.qualifiedName
                        }
                    }

                val usesAssistedInjection =
                    node.constructors.any { constructor ->
                        constructor.hasAnnotation(ASSISTED_INJECT)
                    }

                if (scopeAnnotationsOnCurrentClass.isNotEmpty() && usesAssistedInjection) {
                    scopeAnnotationsOnCurrentClass.forEach { scopeAnnotation ->
                        context.report(
                            issue = ISSUE,
                            location = context.getLocation(scopeAnnotation),
                            message = ISSUE.getExplanation(TextFormat.RAW),
                            quickfixData =
                                fix()
                                    .name("Remove scope annotation")
                                    .replace()
                                    .pattern(
                                        "(?i)(.*${scopeAnnotation.qualifiedName?.substringAfterLast(".")})"
                                    )
                                    .reformat(true)
                                    .with("")
                                    .build(),
                        )
                    }
                }
            }
        }

    companion object {
        private val implementation =
            Implementation(ScopedAssistedInjectedDetector::class.java, Scope.JAVA_FILE_SCOPE)

        internal val ISSUE =
            Issue.create(
                id = "ScopedAssistedInject",
                briefDescription = "Classes using assisted inject cannot be scoped",
                explanation = "Classes using assisted inject cannot be scoped",
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.ERROR,
                implementation = implementation,
            )
    }
}
