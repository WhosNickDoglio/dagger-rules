/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.dagger.detectors

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.BooleanOption
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Incident
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.android.tools.lint.detector.api.StringOption
import com.android.tools.lint.detector.api.TextFormat
import dev.whosnickdoglio.lint.annotations.dagger.INJECT
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UField
import org.jetbrains.uast.getContainingUClass

internal class ConstructorInjectionOverFieldInjectionDetector : Detector(), SourceCodeScanner {
    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitAnnotation(node: UAnnotation) {
                if (node.qualifiedName == INJECT) {
                    val annotatedElement = node.uastParent as? UField ?: return
                    val fullAllowList: List<String> =
                        if (usingAppComponentFactory.getValue(context)) {
                            allowList.getValue(context)?.split(",").orEmpty()
                        } else {
                            allowList.getValue(context)?.split(",").orEmpty() + androidComponents
                        }

                    val isAllowedFieldInjection =
                        fullAllowList.any { className ->
                            context.evaluator.extendsClass(
                                cls = annotatedElement.getContainingUClass(),
                                className = className,
                                strict = true,
                            )
                        }

                    if (!isAllowedFieldInjection) {
                        context.report(
                            Incident(
                                issue = ISSUE,
                                scope = annotatedElement,
                                location = context.getLocation(annotatedElement),
                                message = ISSUE.getExplanation(TextFormat.TEXT),
                            )
                        )
                    }
                }
            }
        }

    companion object {
        private val allowList =
            StringOption(
                name = "allowList",
                description =
                    "Classes that are allowed to use field injection instead of constructor injection.",
                explanation = "",
            )

        internal val usingAppComponentFactory =
            BooleanOption(name = "usingAppComponentFactory", description = "TOOD", explanation = "")

        internal val androidComponents =
            setOf(
                // https://developer.android.com/reference/android/app/AppComponentFactory
                "android.app.Activity",
                "android.app.Fragment",
                "android.app.Application",
                "android.app.Service",
                "android.content.BroadcastReceiver",
                "android.content.ContentProvider",
                // https://developer.android.com/reference/androidx/fragment/app/FragmentFactory
                "androidx.fragment.app.Fragment",
            )

        private val implementation =
            Implementation(
                ConstructorInjectionOverFieldInjectionDetector::class.java,
                Scope.JAVA_FILE_SCOPE,
            )

        internal val ISSUE =
            Issue.create(
                    id = "ConstructorOverField",
                    briefDescription = "Class is using field injection over constructor injection",
                    explanation =
                        """
                    Constructor injection should be favored over field injection for classes that support it.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#prefer-constructor-injection-over-field-injection for more information.
                """,
                    category = Category.CORRECTNESS,
                    priority = 5,
                    severity = Severity.WARNING,
                    implementation = implementation,
                )
                .setOptions(listOf(usingAppComponentFactory, allowList))
    }
}
