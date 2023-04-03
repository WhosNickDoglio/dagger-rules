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
import com.intellij.psi.PsiType
import dev.whosnickdoglio.lint.shared.BINDS
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod

/**
 * A Kotlin and Java [Detector] for Dagger that warns if the parameter of a `@Binds` method is not a
 * subclass of the method return type.
 */
internal class BindsWithCorrectReturnTypeDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitAnnotation(node: UAnnotation) {
                if (node.qualifiedName == BINDS) {
                    val bindsMethod = node.uastParent as? UMethod ?: return
                    val returnType: PsiType? = bindsMethod.returnType
                    val parameter: PsiType? = bindsMethod.parameterList.getParameter(0)?.type

                    if (parameter?.superTypes?.contains(returnType) == false) {
                        context.report(
                            issue = ISSUE,
                            location = context.getLocation(bindsMethod),
                            message = ISSUE.getExplanation(TextFormat.TEXT),
                        )
                    }
                }
            }
        }

    companion object {
        private val implementation =
            Implementation(BindsWithCorrectReturnTypeDetector::class.java, Scope.JAVA_FILE_SCOPE)

        val ISSUE =
            Issue.create(
                id = "BindsWithCorrectReturnType",
                briefDescription = " parameter is not a subclass of return type",
                explanation =
                    """
                        `@Binds` method parameters need to be a subclass of the return type. \
                        Make sure you're passing the correct parameter or the intended subclass is implementing \
                        the return type interface.""",
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.ERROR,
                implementation = implementation
            )
    }
}
