// Copyright (C) 2025 Nicholas Doglio
// SPDX-License-Identifier: MIT
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
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiTypes
import dev.whosnickdoglio.lint.annotations.anvil.CONTRIBUTES_SUBCOMPONENT
import dev.whosnickdoglio.lint.annotations.anvil.MERGE_COMPONENT
import dev.whosnickdoglio.lint.annotations.anvil.MERGE_SUBCOMPONENT
import dev.whosnickdoglio.lint.annotations.dagger.COMPONENT
import dev.whosnickdoglio.lint.annotations.dagger.SUBCOMPONENT
import dev.whosnickdoglio.lint.annotations.hilt.ENTRY_POINT
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

/**
 * A [Detector] that ensures all methods in a Dagger Component (or similar annotations for Anvil and
 * Hilt) are valid and will not fail at compile time. Dagger components support two types of
 * methods, provision methods and member injection methods, anything else will fail to compile.
 *
 * More info can be found in the
 * [Dagger `@Component` documentation](https://dagger.dev/api/latest/dagger/Component.html)
 */
internal class ValidComponentMethodDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitAnnotation(node: UAnnotation) {
                if (node.qualifiedName in components) {
                    val component = node.uastParent as? UClass ?: return
                    component.javaPsi.methods.forEach { method ->
                        val isValidProvisionMethod = method.isValidProvisionMethod()
                        val isValidMemberInjectionMethod = method.isValidMemberInjectionMethod()

                        if (!isValidProvisionMethod && !isValidMemberInjectionMethod) {
                            context.report(
                                Incident()
                                    .issue(ISSUE)
                                    .location(context.getLocation(method))
                                    .message(message(node.qualifiedName?.substringAfterLast(".")))
                            )
                        }
                    }
                }
            }
        }

    private fun message(annotation: String?): String =
        "Methods in a interface annotated with `@${annotation ?: "Component"}` either need to take a " +
            "single parameter with no " +
            "return type (member injection methods) or take no parameters and return a injected or " +
            "provided type (provision methods), anything else will create a compile time error." +
            "See https://whosnickdoglio.dev/dagger-rules/rules/#valid-component-methods " +
            "for more information."

    /**
     * Returns `true` if the method is a valid provision method with no parameter with a non-void
     * return type.
     */
    private fun PsiMethod.isValidProvisionMethod(): Boolean =
        parameterList.isEmpty && returnType != PsiTypes.voidType()

    /**
     * Returns `true` if the method is a valid member injection method with a single parameter with
     * a either a Unit/Void **or** the injected type return type.
     */
    private fun PsiMethod.isValidMemberInjectionMethod(): Boolean {
        val numberOfParameters = parameterList.parametersCount
        val parameter = parameterList.getParameter(0)
        return numberOfParameters == 1 &&
            // Return type can be Unit/Void **or** the injected type
            (returnType == PsiTypes.voidType() || parameter?.type == returnType)
    }

    companion object {
        internal val components =
            setOf(
                // Vanilla Dagger
                COMPONENT,
                SUBCOMPONENT,
                // Anvil
                MERGE_COMPONENT,
                MERGE_SUBCOMPONENT,
                CONTRIBUTES_SUBCOMPONENT,
                // Hilt
                ENTRY_POINT,
            )

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
                implementation = implementation,
            )
    }
}
