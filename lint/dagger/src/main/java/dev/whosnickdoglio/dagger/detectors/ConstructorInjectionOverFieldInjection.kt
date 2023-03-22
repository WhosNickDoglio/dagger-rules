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
package dev.whosnickdoglio.dagger.detectors

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.android.tools.lint.detector.api.StringOption
import dev.whosnickdoglio.dagger.INJECT
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UField
import org.jetbrains.uast.getContainingUClass

internal class ConstructorInjectionOverFieldInjection : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
            override fun visitAnnotation(node: UAnnotation) {
                if (node.qualifiedName == INJECT) {
                    val annotatedElement = node.uastParent
                    if (annotatedElement is UField) {
                        //                        val fullAllowList: List<String> =
                        //                            if (context.mainProject.minSdk >= MIN_SDK) {
                        //
                        // allowList.defaultValue?.split(",").orEmpty()
                        //                            } else {
                        //
                        // allowList.defaultValue?.split(",").orEmpty() + androidComponents
                        //                            }

                        val isAllowedFieldInjection =
                            androidComponents.any { className ->
                                context.evaluator.extendsClass(
                                    cls = annotatedElement.getContainingUClass(),
                                    className = className,
                                    strict = true // todo what you do
                                )
                            }

                        //                        minSdkLessThan(28)
                        if (!isAllowedFieldInjection) {
                            context.report(
                                issue = ISSUE,
                                location = context.getLocation(annotatedElement),
                                message = "plz use constructor injection over field injection"
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {

        //        private const val MIN_SDK = 28

        // TODO make this configurable
        @Suppress("UnusedPrivateMember")
        private val allowList =
            StringOption(
                name = "allowList",
                description =
                    "Classes that are allowed to use field injection instead of constructor injection.",
                explanation = ""
            )

        //
        private val androidComponents =
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
                ConstructorInjectionOverFieldInjection::class.java,
                Scope.JAVA_FILE_SCOPE
            )

        val ISSUE =
            Issue.create(
                id = "ConstructorOverField",
                briefDescription = "Hello friend",
                explanation = "Hello friend",
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.WARNING,
                implementation = implementation
            )
    }
}
