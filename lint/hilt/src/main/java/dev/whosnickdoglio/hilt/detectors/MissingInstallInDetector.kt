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
import com.android.tools.lint.detector.api.TextFormat
import dev.whosnickdoglio.hilt.ENTRY_POINT
import dev.whosnickdoglio.hilt.INSTALL_IN
import dev.whosnickdoglio.lint.shared.MODULE
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

// TODO default to SingletonComponent
internal class MissingInstallInDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitAnnotation(node: UAnnotation) {
                if (node.qualifiedName == MODULE || node.qualifiedName == ENTRY_POINT) {
                    val daggerModule = node.uastParent as? UClass ?: return

                    if (!daggerModule.hasAnnotation(INSTALL_IN)) {
                        context.report(
                            issue = ISSUE,
                            location = context.getNameLocation(daggerModule),
                            message = ISSUE.getExplanation(TextFormat.TEXT),
                            quickfixData =
                                fix()
                                    .name("Add @InstallIn annotation")
                                    .annotate(INSTALL_IN)
                                    .range(context.getNameLocation(node))
                                    .build()
                        )
                    }
                }
            }
        }

    companion object {
        private val implementation =
            Implementation(MissingInstallInDetector::class.java, Scope.JAVA_FILE_SCOPE)

        internal val ISSUE =
            Issue.create(
                id = "MissingInstallInAnnotation",
                briefDescription = "Missing @InstallIn annotation",
                explanation =
                    """
                    Hilt modules and entry points require the `@InstallIn` annotation to be properly connected to a Component. Annotate this class with @InstallIn \
                    and the Hilt component you want to connect it to, the most commonly used Component is the `SingletonComponent`.
                    """,
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.ERROR,
                implementation = implementation
            )
    }
}
