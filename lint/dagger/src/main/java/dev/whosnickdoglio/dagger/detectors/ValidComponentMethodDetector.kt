/*
 * Copyright (C) 2023 Nicholas Doglio
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
import com.intellij.psi.PsiTypes
import dev.whosnickdoglio.lint.shared.COMPONENT
import dev.whosnickdoglio.lint.shared.SUBCOMPONENT
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod

// TODO can apply this to EntryPoint and ContributesTo
internal class ValidComponentMethodDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitAnnotation(node: UAnnotation) {
                if (node.qualifiedName == COMPONENT || node.qualifiedName == SUBCOMPONENT) {
                    val component = node.uastParent as? UClass ?: return

                    component.methods.forEach { method ->
                        val isValidProvisionMethod = method.isValidProvisionMethod()
                        val isValidMemberInjectionMethod = method.isValidMemberInjectionMethod()

                        if (!isValidProvisionMethod && !isValidMemberInjectionMethod) {
                            context.report(
                                Incident(
                                    ISSUE,
                                    context.getLocation(method),
                                    ISSUE.getExplanation(TextFormat.RAW)
                                )
                            )
                        }
                    }
                }
            }
        }

    // no parameter with actual return type
    private fun UMethod.isValidProvisionMethod(): Boolean =
        parameterList.isEmpty && returnType != PsiTypes.voidType()

    // One parameter with a Unit/Void return type
    private fun UMethod.isValidMemberInjectionMethod(): Boolean {
        // Return type can be the same type as injected type
        val numberOfParameters = parameterList.parametersCount
        return numberOfParameters == 1 && returnType == PsiTypes.voidType()
    }

    companion object {
        private val implementation =
            Implementation(ValidComponentMethodDetector::class.java, Scope.JAVA_FILE_SCOPE)

        internal val ISSUE =
            Issue.create(
                id = "ValidComponentMethod",
                briefDescription = "Invalid `@Component` method",
                explanation =
                    "Methods in a `@Component` interface either need to take a single parameter with no " +
                        "return type (member injection methods) or take no parameters and return a injected or " +
                        "provided type (provision methods), anything else will create a compile time error." +
                        "See https://whosnickdoglio.dev/dagger-rules/rules/#valid-component-methods " +
                        "for more information.",
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.ERROR,
                implementation = implementation
            )
    }
}
