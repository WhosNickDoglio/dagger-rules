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
import com.android.tools.lint.detector.api.asCall
import com.intellij.psi.util.InheritanceUtil
import dev.whosnickdoglio.lint.annotations.dagger.PROVIDES
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.toUElementOfType
import org.jetbrains.uast.util.isConstructorCall

// TODO make this configurable

internal class FavorBindsOverProvidesDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitAnnotation(node: UAnnotation) {
                if (node.qualifiedName == PROVIDES) {
                    // TODO check if provided class has `@Inject` on it's constructor
                    val providesMethod = node.uastParent as? UMethod ?: return

                    val parameterIsReturnedAsSuper =
                        providesMethod.checkIfMethodJustPassesThroughParameter(context)

                    val constructorCall =
                        providesMethod.checkIfMethodBodyJustCallsConstructor(context)

                    if (parameterIsReturnedAsSuper || constructorCall) {
                        context.report(
                            issue = ISSUE,
                            location = context.getNameLocation(providesMethod),
                            message = ISSUE.getExplanation(TextFormat.TEXT),
                        )
                    }
                }
            }
        }

    private fun UMethod.checkIfMethodJustPassesThroughParameter(context: JavaContext): Boolean {
        val methodReturnType = context.evaluator.getTypeClass(this.returnType)
        val parameterType = context.evaluator.getTypeClass(this.uastParameters.firstOrNull()?.type)

        // CHeck if type is specifically the same type and not a supertype
        return InheritanceUtil.isInheritor(
            parameterType,
            true,
            methodReturnType?.qualifiedName.orEmpty(),
        )
    }

    private fun UMethod.checkIfMethodBodyJustCallsConstructor(context: JavaContext): Boolean {
        val methodReturnType =
            context.evaluator.getTypeClass(this.returnType)?.toUElementOfType<UClass>()

        val bodyReturnType =
            context.evaluator
                .getTypeClass(uastBody?.asCall()?.returnType)
                ?.toUElementOfType<UClass>()

        val isConstructorCall = uastBody?.isConstructorCall() == true

        val bodyReturnsSubClassOfReturnType =
            InheritanceUtil.isInheritor(
                bodyReturnType,
                true,
                methodReturnType?.qualifiedName.orEmpty(),
            )
        val returnCondition = isConstructorCall && bodyReturnsSubClassOfReturnType

        return returnCondition
    }

    companion object {

        internal val allowList = setOf("Factory", "Builder")

        private val implementation =
            Implementation(FavorBindsOverProvidesDetector::class.java, Scope.JAVA_FILE_SCOPE)
        val ISSUE =
            Issue.create(
                    id = "FavorBindsOverProvides",
                    briefDescription = "Using @Provides instead of  @Binds",
                    explanation = "plz use @Binds instead of @Provides",
                    category = Category.CORRECTNESS,
                    priority = 5,
                    severity = Severity.WARNING,
                    implementation = implementation,
                )
                .setEnabledByDefault(false)
    }
}
