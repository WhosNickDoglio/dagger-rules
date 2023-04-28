/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.hilt.detectors

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import dev.whosnickdoglio.hilt.ANDROID_ENTRY_POINT
import dev.whosnickdoglio.hilt.HILT_VIEW_MODEL
import dev.whosnickdoglio.lint.shared.INJECT
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

internal class MissingHiltAnnotationDetector : Detector(), SourceCodeScanner {

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
                if (
                    context.evaluator.extendsClass(node, "androidx.lifecycle.ViewModel", true) &&
                        node.hasInjectedConstructor() &&
                        !node.hasAnnotation(HILT_VIEW_MODEL)
                ) {
                    context.report(
                        issue = ISSUE,
                        location = context.getNameLocation(node),
                        message =
                            "This class is missing the `@${HILT_VIEW_MODEL.substringAfterLast(".")}`",
                        quickfixData =
                            fix()
                                .name("Add ${HILT_VIEW_MODEL.substringAfterLast(".")} annotation")
                                .annotate(HILT_VIEW_MODEL)
                                .range(context.getNameLocation(node))
                                .build(),
                    )
                } else {
                    context.checkAndroidEntryPoints(node)
                }
            }
        }

    private fun JavaContext.checkAndroidEntryPoints(node: UClass) {
        androidEntryPointSupers.forEach { superClass ->
            val isSubClass = evaluator.extendsClass(node, superClass, true)

            val injectedFields =
                node.fields.filter { field ->
                    field.uAnnotations.any { annotation -> annotation.qualifiedName == INJECT }
                }

            if (
                isSubClass &&
                    injectedFields.isNotEmpty() &&
                    !node.hasAnnotation(ANDROID_ENTRY_POINT)
            ) {
                report(
                    issue = ISSUE,
                    location = getNameLocation(node),
                    message =
                        "This class is missing the `@${ANDROID_ENTRY_POINT.substringAfterLast(".")}`",
                    quickfixData =
                        fix()
                            .name("Add ${ANDROID_ENTRY_POINT.substringAfterLast(".")} annotation")
                            .annotate(ANDROID_ENTRY_POINT)
                            .range(getNameLocation(node))
                            .build(),
                )
            }
        }
    }

    private fun UClass.hasInjectedConstructor(): Boolean =
        constructors.any { method -> method.hasAnnotation(INJECT) }

    companion object {
        private val implementation =
            Implementation(MissingHiltAnnotationDetector::class.java, Scope.JAVA_FILE_SCOPE)

        internal val ISSUE =
            Issue.create(
                id = "MissingHiltAnnotation",
                briefDescription = "Android Component is missing Hilt annotation",
                explanation = "Hello friend",
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.ERROR,
                implementation = implementation
            )
    }
}
