/*
 * Copyright (C) 2025 Nicholas Doglio
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
import dev.whosnickdoglio.lint.annotations.dagger.BINDS
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod

/**
 * A Kotlin and Java [Detector] for Dagger that warns if the parameter of a `@Binds` method is not a
 * subclass of the method return type.
 */
internal class CorrectBindsUsageDetector : Detector(), SourceCodeScanner {
    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitAnnotation(node: UAnnotation) {
                if (node.qualifiedName == BINDS) {
                    val bindsMethod = node.uastParent as? UMethod ?: return

                    if (!context.evaluator.isAbstract(bindsMethod)) {
                        context.report(
                            Incident(
                                issue = ISSUE_BINDS_ABSTRACT,
                                scope = bindsMethod,
                                location = context.getLocation(bindsMethod),
                                message = ISSUE_BINDS_ABSTRACT.getExplanation(TextFormat.TEXT),
                            )
                        )
                    }

                    val returnType = bindsMethod.returnType
                    val parameter = bindsMethod.parameterList.getParameter(0)?.type

                    if (parameter?.superTypes?.contains(returnType) == false) {
                        context.report(
                            Incident(
                                issue = ISSUE_CORRECT_RETURN_TYPE,
                                scope = bindsMethod,
                                location = context.getLocation(bindsMethod),
                                message = ISSUE_CORRECT_RETURN_TYPE.getExplanation(TextFormat.TEXT),
                            )
                        )
                    }
                }
            }
        }

    companion object {
        private val implementation =
            Implementation(CorrectBindsUsageDetector::class.java, Scope.JAVA_FILE_SCOPE)

        internal val ISSUE_CORRECT_RETURN_TYPE =
            Issue.create(
                id = "BindsWithCorrectReturnType",
                briefDescription = " parameter is not a subclass of return type",
                explanation =
                    """
                        `@Binds` method parameters need to be a subclass of the return type. \
                        Make sure you're passing the correct parameter or the intended subclass is implementing \
                        the return type interface.

                        See https://whosnickdoglio.dev/dagger-rules/rules/#a-binds-method-parameter-should-be-a-subclass-of-its-return-type for more information.
                        """,
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.ERROR,
                implementation = implementation,
            )

        internal val ISSUE_BINDS_ABSTRACT =
            Issue.create(
                id = "BindsMustBeAbstract",
                briefDescription = "@Binds method must be abstract",
                explanation =
                    """
                    A @Binds method needs to be abstract or Dagger will throw an error at compile time. \
                    See https://whosnickdoglio.dev/dagger-rules/rules/#methods-annotated-with-binds-must-be-abstract for more information.
                """,
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.ERROR,
                implementation = implementation,
            )
    }
}
