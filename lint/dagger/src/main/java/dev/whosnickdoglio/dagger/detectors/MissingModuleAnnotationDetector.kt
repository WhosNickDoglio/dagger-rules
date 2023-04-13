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
import com.android.tools.lint.detector.api.isKotlin
import dev.whosnickdoglio.lint.shared.BINDS
import dev.whosnickdoglio.lint.shared.MODULE
import dev.whosnickdoglio.lint.shared.MULTIBINDS
import dev.whosnickdoglio.lint.shared.PROVIDES
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UElement
import org.jetbrains.uast.getContainingUClass

internal class MissingModuleAnnotationDetector : Detector(), SourceCodeScanner {

    private val daggerAnnotations = listOf(BINDS, PROVIDES, MULTIBINDS)

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
            override fun visitAnnotation(node: UAnnotation) {
                if (node.qualifiedName in daggerAnnotations) {
                    val containingClass = node.uastParent?.getContainingUClass() ?: return

                    if (isKotlin(node.lang) && context.evaluator.isCompanion(containingClass)) {
                        // Early out, other methods should already trigger lint warning?
                        return
                    }

                    if (
                        !containingClass.uAnnotations.any { annotation ->
                            annotation.qualifiedName == MODULE
                        }
                    ) {
                        context.report(
                            issue = ISSUE,
                            location = context.getNameLocation(containingClass),
                            message = "Don't forget the `@Module` annotation!",
                            quickfixData =
                                fix()
                                    .name("Add @Module annotation")
                                    .annotate(MODULE)
                                    .range(context.getNameLocation(containingClass))
                                    .build()
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
                    TODO talk about needing @Module
                    """,
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.ERROR,
                implementation = implementation
            )
    }
}
