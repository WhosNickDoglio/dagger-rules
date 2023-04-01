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
import dev.whosnickdoglio.lint.shared.BINDS
import dev.whosnickdoglio.lint.shared.INTO_MAP
import dev.whosnickdoglio.lint.shared.INTO_SET
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UElement

internal class FavorContributesBindingOverBindsDetector : Detector(), SourceCodeScanner {

    private val multiBindsAnnotations = setOf(INTO_MAP, INTO_SET)

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        // Anvil is Kotlin only
        if (!isKotlin(context.uastFile?.lang)) return null
        return object : UElementHandler() {
            override fun visitAnnotation(node: UAnnotation) {
                if (node.qualifiedName == BINDS) {
                    //                    val bindsMethod = node.uastParent
                    //                    if (bindsMethod is UMethod) {
                    //                        val returnType = bindsMethod.returnType
                    //                    }
                    // TODO check for generics in return type
                    context.report(
                        issue = ISSUE,
                        // TODO try range location
                        location = context.getLocation(node.uastParent),
                        message = "You can use `@ContributesBinding` over `@Binds`"
                    )
                    // Multibinding
                } else if (node.qualifiedName in multiBindsAnnotations) {
                    context.report(
                        issue = ISSUE,
                        location = context.getLocation(node.uastParent),
                        message = "You can use `@ContributesBinding` over `@Binds`"
                    )
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
        val ISSUE =
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
