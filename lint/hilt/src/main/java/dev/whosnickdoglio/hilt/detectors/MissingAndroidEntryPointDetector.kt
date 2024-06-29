/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.hilt.detectors

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
import dev.whosnickdoglio.lint.shared.dagger.INJECT
import dev.whosnickdoglio.lint.shared.hilt.ANDROID_ENTRY_POINT
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

internal class MissingAndroidEntryPointDetector :
    Detector(),
    SourceCodeScanner {
    private val androidEntryPointSupers =
        setOf(
            "android.app.Activity",
            "android.app.Fragment",
            "android.app.Service",
            "android.content.ContentProvider",
            "android.content.BroadcastReceiver",
            "androidx.fragment.app.Fragment",
        )

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitClass(node: UClass) {
                androidEntryPointSupers.forEach { superClass ->
                    val isSubClass = context.evaluator.extendsClass(node, superClass, true)
                    val injectedFields =
                        node.fields.filter { field ->
                            field.uAnnotations.any { annotation ->
                                annotation.qualifiedName == INJECT
                            }
                        }

                    val isMissingAndroidEntryPointAnnotation =
                        !node.hasAnnotation(ANDROID_ENTRY_POINT)
                    if (
                        isSubClass &&
                        injectedFields.isNotEmpty() &&
                        isMissingAndroidEntryPointAnnotation
                    ) {
                        context.report(
                            Incident(context, ISSUE)
                                .location(context.getNameLocation(node))
                                .message(ISSUE.getExplanation(TextFormat.RAW))
                                .fix(
                                    fix()
                                        .name(
                                            "Add ${ANDROID_ENTRY_POINT.substringAfterLast(".")} annotation",
                                        )
                                        .annotate(ANDROID_ENTRY_POINT, context, node)
                                        .autoFix(robot = true, independent = true)
                                        .build(),
                                ),
                        )
                    }
                }
            }
        }

    companion object {
        private val implementation =
            Implementation(MissingAndroidEntryPointDetector::class.java, Scope.JAVA_FILE_SCOPE)

        internal val ISSUE =
            Issue.create(
                id = "MissingAndroidEntryPointAnnotation",
                briefDescription = "Android Component is missing @AndroidEntryPoint annotation",
                explanation =
                """
                    This class needs to be annotated with `@AndroidEntryPoint` to use field injection with Hilt.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#android-components-should-be-annotated-with-androidentrypoint for more information.
                """,
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.ERROR,
                implementation = implementation,
            )
    }
}
