/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.anvil.detectors

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
import com.android.tools.lint.detector.api.StringOption
import com.android.tools.lint.detector.api.TextFormat
import dev.whosnickdoglio.lint.annotations.anvil.CONTRIBUTES_TO
import dev.whosnickdoglio.lint.annotations.dagger.MODULE
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.kotlin.isKotlin

// TODO make this configurable for Anvil scopes in quick fix
internal class MissingContributesToDetector : Detector(), SourceCodeScanner {
    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        // Anvil is Kotlin only
        if (!isKotlin(context.uastFile?.lang)) return null
        return object : UElementHandler() {
            override fun visitAnnotation(node: UAnnotation) {
                if (node.qualifiedName == MODULE) {
                    val element = node.uastParent as? UClass ?: return

                    if (!element.hasAnnotation(CONTRIBUTES_TO)) {
                        val anvilScopes =
                            customAnvilScopes.getValue(context).orEmpty().split(",").filter {
                                it.isNotEmpty()
                            }

                        context.report(
                            Incident(context, ISSUE)
                                .location(context.getNameLocation(element))
                                .message(ISSUE.getExplanation(TextFormat.RAW))
                                .fix(
                                    if (anvilScopes.isEmpty()) {
                                        fix()
                                            .name("Add @ContributesTo annotation")
                                            .annotate(CONTRIBUTES_TO, context, element)
                                            .autoFix(robot = true, independent = true)
                                            .build()
                                    } else {
                                        fix()
                                            .alternatives()
                                            .apply {
                                                anvilScopes.forEach { scope ->
                                                    add(
                                                        fix()
                                                            .name(
                                                                "Contribute to ${scope.substringAfterLast(".")} "
                                                            )
                                                            .annotate(
                                                                "$CONTRIBUTES_TO($scope::class)",
                                                                context,
                                                                element,
                                                            )
                                                            .autoFix(
                                                                robot = true,
                                                                independent = true,
                                                            )
                                                            .build()
                                                    )
                                                }
                                            }
                                            .build()
                                    }
                                )
                        )
                    }
                }
            }
        }
    }

    companion object {
        private val implementation =
            Implementation(MissingContributesToDetector::class.java, Scope.JAVA_FILE_SCOPE)

        internal const val CUSTOM_ANVIL_SCOPE_OPTION_KEY = "anvilScopes"

        private val customAnvilScopes =
            StringOption(
                name = CUSTOM_ANVIL_SCOPE_OPTION_KEY,
                description = "A comma separated list of fully qualified custom Hilt components",
                explanation =
                    "Hilt provides you the ability to define custom Components if the " +
                        "preexisting ones don't work for your use case, If you have any custom Hilt components " +
                        "defined they can be added to the quickfix suggestions with this option. ",
            )

        internal val ISSUE =
            Issue.create(
                    id = "MissingContributesToAnnotation",
                    briefDescription = "Module missing @ContributesTo annotation",
                    explanation =
                        """
                    This Dagger module is missing a `@ContributesTo` annotation for Anvil to pick it up. See https://whosnickdoglio.dev/dagger-rules/rules/#a-class-annotated-with-module-should-also-be-annotated-with-contributesto for more information.
                    """,
                    category = Category.CORRECTNESS,
                    priority = 5,
                    severity = Severity.ERROR,
                    implementation = implementation,
                )
                .setOptions(listOf(customAnvilScopes))
    }
}
