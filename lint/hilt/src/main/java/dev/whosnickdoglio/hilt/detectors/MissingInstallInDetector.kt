/*
 * Copyright (C) 2025 Nicholas Doglio
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
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.android.tools.lint.detector.api.StringOption
import com.android.tools.lint.detector.api.TextFormat
import com.android.tools.lint.detector.api.isKotlin
import com.intellij.psi.PsiElement
import dev.whosnickdoglio.lint.annotations.dagger.MODULE
import dev.whosnickdoglio.lint.annotations.hilt.ENTRY_POINT
import dev.whosnickdoglio.lint.annotations.hilt.INSTALL_IN
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

internal class MissingInstallInDetector : Detector(), SourceCodeScanner {
    private val defaultHiltComponents =
        setOf(
            "dagger.hilt.components.SingletonComponent",
            "dagger.hilt.android.components.ActivityComponent",
            "dagger.hilt.android.components.ActivityRetainedComponent",
            "dagger.hilt.android.components.FragmentComponent",
            "dagger.hilt.android.components.ServiceComponent",
            "dagger.hilt.android.components.ViewComponent",
            "dagger.hilt.android.components.ViewModelComponent",
            "dagger.hilt.android.components.ViewWithFragmentComponent",
        )

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitAnnotation(node: UAnnotation) {
                if (node.qualifiedName == MODULE || node.qualifiedName == ENTRY_POINT) {
                    val uClass = node.uastParent as? UClass ?: return
                    val daggerModule = uClass.javaPsi

                    if (!daggerModule.hasAnnotation(INSTALL_IN)) {
                        context.report(
                            Incident(context, ISSUE)
                                .location(context.getNameLocation(daggerModule))
                                .message(ISSUE.getExplanation(TextFormat.RAW))
                                .fix(
                                    fix()
                                        .alternatives()
                                        .apply {
                                            uClass.sourcePsi?.let { psi ->
                                                quickFixes(context, psi).forEach { fix -> add(fix) }
                                            }
                                        }
                                        .build()
                                )
                        )
                    }
                }
            }
        }

    private fun quickFixes(context: JavaContext, classToBeAnnotated: PsiElement): List<LintFix> {
        val customHiltComponents =
            customComponentOptions.getValue(context).orEmpty().split(",").toSet().filter {
                it.isNotEmpty()
            }

        return (defaultHiltComponents + customHiltComponents).map { component ->
            val separator = if (isKotlin(classToBeAnnotated.language)) "::" else "."

            fix()
                .name("Install in the ${component.substringAfterLast(".")} ")
                .annotate("$INSTALL_IN($component${separator}class)", context, classToBeAnnotated)
                .autoFix(robot = true, independent = true)
                .build()
        }
    }

    companion object {
        internal const val CUSTOM_HILT_COMPONENTS_OPTION_KEY = "customHiltComponents"

        private val customComponentOptions =
            StringOption(
                name = CUSTOM_HILT_COMPONENTS_OPTION_KEY,
                description = "A comma separated list of fully qualified custom Hilt components",
                explanation =
                    "Hilt provides you the ability to define custom Components if the " +
                        "preexisting ones don't work for your use case, If you have any custom Hilt components " +
                        "defined they can be added to the quickfix suggestions with this option. ",
            )

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

                    See https://whosnickdoglio.dev/dagger-rules/rules/#a-class-annotated-with-module-or-entrypoint-should-also-be-annotated-with-installin for more information.
                    """,
                    category = Category.CORRECTNESS,
                    priority = 5,
                    severity = Severity.ERROR,
                    implementation = implementation,
                )
                .setOptions(listOf(customComponentOptions))
    }
}
