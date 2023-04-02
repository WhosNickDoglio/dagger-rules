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
package dev.whosnickdoglio.dagger

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import dev.whosnickdoglio.dagger.detectors.BindsWithCorrectReturnTypeDetector
import dev.whosnickdoglio.dagger.detectors.ConstructorInjectionOverFieldInjectionDetector
import dev.whosnickdoglio.dagger.detectors.ConstructorInjectionOverProvidesDetector
import dev.whosnickdoglio.dagger.detectors.FavorBindsOverProvidesDetector
import dev.whosnickdoglio.dagger.detectors.MissingModuleAnnotationDetector
import dev.whosnickdoglio.dagger.detectors.StaticProvidesDetector

class DaggerRulesIssueRegistry : IssueRegistry() {

    override val issues: List<Issue> =
        listOf(
            BindsWithCorrectReturnTypeDetector.ISSUE,
            ConstructorInjectionOverFieldInjectionDetector.ISSUE,
            ConstructorInjectionOverProvidesDetector.ISSUE,
            FavorBindsOverProvidesDetector.ISSUE,
            MissingModuleAnnotationDetector.ISSUE,
            StaticProvidesDetector.ISSUE,
        )

    override val api: Int = CURRENT_API

    override val vendor: Vendor
        get() =
            Vendor(
                vendorName = "Nicholas Doglio",
                identifier = "dev.whosnickdoglio.dagger.rules:dagger-lint",
                feedbackUrl = "https://github.com/WhosNickDoglio/dagger-rules/issues"
            )
}
