/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.dagger

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import dev.whosnickdoglio.dagger.detectors.BindsWithCorrectReturnTypeDetector
import dev.whosnickdoglio.dagger.detectors.ConstructorInjectionOverFieldInjectionDetector
import dev.whosnickdoglio.dagger.detectors.MissingModuleAnnotationDetector
import dev.whosnickdoglio.dagger.detectors.StaticProvidesDetector

class DaggerRulesIssueRegistry : IssueRegistry() {

    override val issues: List<Issue> =
        listOf(
            BindsWithCorrectReturnTypeDetector.ISSUE,
            ConstructorInjectionOverFieldInjectionDetector.ISSUE,
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
