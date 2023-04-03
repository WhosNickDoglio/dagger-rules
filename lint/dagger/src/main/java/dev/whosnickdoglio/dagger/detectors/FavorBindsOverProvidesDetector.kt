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
import com.intellij.psi.util.InheritanceUtil
import dev.whosnickdoglio.lint.shared.PROVIDES
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod

internal class FavorBindsOverProvidesDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitAnnotation(node: UAnnotation) {
                if (node.qualifiedName == PROVIDES) {
                    val providesMethod = node.uastParent as? UMethod ?: return

                    providesMethod.uastParameters.firstOrNull()?.type

                    val methodReturnType = providesMethod.returnType

                    val parameterType = providesMethod.uastParameters.firstOrNull()?.type

                    if (
                        InheritanceUtil.isInheritor(
                            parameterType,
                            methodReturnType?.canonicalText.orEmpty()
                        )
                    ) {
                        context.report(
                            issue = ISSUE,
                            location = context.getLocation(providesMethod),
                            message = "Hello hello hello"
                        )
                    }
                }
            }
        }

    companion object {

        private val implementation =
            Implementation(FavorBindsOverProvidesDetector::class.java, Scope.JAVA_FILE_SCOPE)
        val ISSUE =
            Issue.create(
                id = "FavorBindsOverProvides",
                briefDescription = "Hello friend",
                explanation = "Hello friend",
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.WARNING,
                implementation = implementation
            )
    }
}
