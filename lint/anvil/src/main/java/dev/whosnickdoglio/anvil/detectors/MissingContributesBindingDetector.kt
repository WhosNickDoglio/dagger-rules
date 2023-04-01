/*
 * MIT License
 *
 * Copyright (c) 2023 Nicholas Doglio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.whosnickdoglio.anvil.detectors

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.android.tools.lint.detector.api.isKotlin
import dev.whosnickdoglio.anvil.CONTRIBUTES_BINDING
import dev.whosnickdoglio.anvil.CONTRIBUTES_MULTI_BINDING
import dev.whosnickdoglio.lint.shared.INJECT
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

internal class MissingContributesBindingDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        // Anvil is Kotlin only
        if (!isKotlin(context.uastFile?.lang)) return null
        return object : UElementHandler() {
            override fun visitClass(node: UClass) {
                val psiClass = node.javaPsi

                val doesNotHaveBindingAnnotations =
                    !node.uAnnotations.any { annotation ->
                        annotation.qualifiedName == CONTRIBUTES_BINDING ||
                            annotation.qualifiedName == CONTRIBUTES_MULTI_BINDING
                    }
                if (
                    psiClass.constructors.any { method -> method.hasAnnotation(INJECT) } &&
                        // TODO this feels naive
                        node.superTypes.size > 1 &&
                        doesNotHaveBindingAnnotations
                // TODO ContributesBinding doesn't support generics so we should avoid suggesting
                // when the super takes generics
                ) {
                    context.report(
                        issue = ISSUE,
                        location = context.getNameLocation(node),
                        message =
                            "Contribute this binding to the Dagger graph using an Anvil annotation",
                        quickfixData =
                            fix()
                                .alternatives(
                                    fix()
                                        .name("Add @ContributesBinding annotation")
                                        .annotate(CONTRIBUTES_BINDING)
                                        .range(context.getNameLocation(node))
                                        .build(),
                                    fix()
                                        .name("Add @ContributesMultibinding annotation")
                                        .annotate(CONTRIBUTES_MULTI_BINDING)
                                        .range(context.getNameLocation(node))
                                        .build(),
                                )
                    )
                }
            }
        }
    }

    companion object {
        private val implementation =
            Implementation(MissingContributesBindingDetector::class.java, Scope.JAVA_FILE_SCOPE)
        val ISSUE =
            Issue.create(
                id = "MissingContributesBindingAnnotation",
                briefDescription = "Hello friend",
                explanation = "Hello friend",
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.WARNING,
                implementation = implementation
            )
    }
}
