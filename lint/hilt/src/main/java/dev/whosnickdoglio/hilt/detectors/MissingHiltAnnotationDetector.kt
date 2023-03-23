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
import dev.whosnickdoglio.hilt.HILT_ANDROID_APP
import dev.whosnickdoglio.hilt.HILT_VIEW_MODEL
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

internal class MissingHiltAnnotationDetector : Detector(), SourceCodeScanner {

    private val supersToAnnotation =
        mapOf(
            "android.app.Activity" to ANDROID_ENTRY_POINT,
            "android.app.Fragment" to ANDROID_ENTRY_POINT,
            "android.app.Service" to ANDROID_ENTRY_POINT,
            "android.content.ContentProvider" to ANDROID_ENTRY_POINT,
            "android.content.BroadcastReceiver" to ANDROID_ENTRY_POINT,
            "androidx.fragment.app.Fragment" to ANDROID_ENTRY_POINT,
            "android.app.Application" to HILT_ANDROID_APP,
            "androidx.lifecycle.ViewModel" to HILT_VIEW_MODEL
        )

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitClass(node: UClass) {
                supersToAnnotation.forEach { (superClass, hiltAnnotation) ->
                    val isSubClass = context.evaluator.extendsClass(node, superClass, true)
                    if (
                        isSubClass &&
                            !node.uAnnotations.any { uAnnotation ->
                                uAnnotation.qualifiedName == hiltAnnotation
                            }
                    ) {
                        context.report(
                            issue = ISSUE,
                            location = context.getNameLocation(node),
                            message = "",
                            quickfixData = null // TODO
                        )
                    }
                }
            }
        }

    companion object {
        private val implementation =
            Implementation(MissingHiltAnnotationDetector::class.java, Scope.JAVA_FILE_SCOPE)
        val ISSUE =
            Issue.create(
                id = "MissingHiltAnnotation",
                briefDescription = "Hello friend",
                explanation = "Hello friend",
                category = Category.CORRECTNESS,
                priority = 5,
                severity = Severity.ERROR,
                implementation = implementation
            )
    }
}
