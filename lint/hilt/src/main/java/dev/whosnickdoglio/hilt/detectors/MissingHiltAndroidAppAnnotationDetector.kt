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
import dev.whosnickdoglio.hilt.HILT_ANDROID_APP
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

internal class MissingHiltAndroidAppAnnotationDetector :
    Detector(),
    SourceCodeScanner {
    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitClass(node: UClass) {
                if (
                    context.evaluator.extendsClass(node, ANDROID_APP, true) &&
                    !node.hasAnnotation(HILT_ANDROID_APP)
                ) {
                    context.report(
                        Incident(context, ISSUE)
                            .location(context.getNameLocation(node))
                            .message(ISSUE.getBriefDescription(TextFormat.RAW))
                            .fix(
                                fix()
                                    .name(
                                        "Add ${HILT_ANDROID_APP.substringAfterLast(".")} annotation",
                                    )
                                    .annotate(HILT_ANDROID_APP, context, node)
                                    .autoFix(robot = true, independent = true)
                                    .build(),
                            ),
                    )
                }
            }
        }

    companion object {
        private const val ANDROID_APP = "android.app.Application"

        private val implementation =
            Implementation(
                MissingHiltAndroidAppAnnotationDetector::class.java,
                Scope.JAVA_FILE_SCOPE,
            )

        val ISSUE =
            Issue.create(
                id = "MissingHiltAndroidAppAnnotation",
                briefDescription = "`Application` subclasses need `@HiltAndroidApp`",
                explanation =
                """
                    When you using Hilt it's required for a `Application` subclass to be annotated with `@HiltAndroidApp.`

                    See https://whosnickdoglio.dev/dagger-rules/rules/#application-subclasses-should-be-annotated-with-hiltandroidapp for more information.
                    """,
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.ERROR,
                implementation = implementation,
            )
    }
}
