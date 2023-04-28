/*
 * Copyright (C) 2023 Nicholas Doglio
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
import com.android.tools.lint.detector.api.isKotlin
import dev.whosnickdoglio.lint.shared.BINDS
import dev.whosnickdoglio.lint.shared.INTO_MAP
import dev.whosnickdoglio.lint.shared.INTO_SET
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod

/**
 * A Kotlin only [Detector] for the Anvil library to suggest using the provided
 * `@ContributesBinding` or `@ContributesMultibinding` annotations instead of using a Dagger
 * `@Module` to bind the implementation to an interface.
 */
internal class FavorContributesBindingOverBindsDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        // Anvil is Kotlin only
        if (!isKotlin(context.uastFile?.lang)) return null
        return object : UElementHandler() {
            override fun visitAnnotation(node: UAnnotation) {
                if (node.qualifiedName == BINDS) {
                    val method = node.uastParent as? UMethod ?: return
                    val returnType = context.evaluator.getTypeClass(method.returnType)
                    // Anvils binding annotations do not support super types that type parameters
                    // TODO document this better
                    if (returnType?.typeParameters?.isEmpty() == true) {
                        if (method.hasAnnotation(INTO_MAP) || method.hasAnnotation(INTO_SET)) {
                            context.report(
                                Incident(context, ISSUE)
                                    // TODO try range location
                                    .location(context.getLocation(node.uastParent))
                                    .message("You can use `@ContributesMultibinding` over `@Binds`")
                            )
                        } else {
                            context.report(
                                Incident(context, ISSUE)
                                    // TODO try range location
                                    .location(context.getLocation(node.uastParent))
                                    .message("You can use `@ContributesBinding` over `@Binds`")
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val implementation =
            Implementation(
                FavorContributesBindingOverBindsDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )

        internal val ISSUE =
            Issue.create(
                id = "ContributesBindingOverBinds",
                briefDescription = "Hello friend",
                explanation = "Hello friend",
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.WARNING,
                implementation = implementation
            )
    }
}
