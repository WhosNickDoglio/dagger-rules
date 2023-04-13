/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.dagger

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import dev.whosnickdoglio.dagger.detectors.ComponentMustBeAbstractDetector
import dev.whosnickdoglio.dagger.detectors.ConstructorInjectionOverFieldInjectionDetector
import dev.whosnickdoglio.dagger.detectors.CorrectBindsUsageDetector
import dev.whosnickdoglio.dagger.detectors.MissingModuleAnnotationDetector
import dev.whosnickdoglio.dagger.detectors.StaticProvidesDetector

class DaggerRulesIssueRegistry : IssueRegistry() {

    override val issues: List<Issue> =
        listOf(
            ComponentMustBeAbstractDetector.ISSUE,
            ConstructorInjectionOverFieldInjectionDetector.ISSUE,
            CorrectBindsUsageDetector.ISSUE_BINDS_ABSTRACT,
            CorrectBindsUsageDetector.ISSUE_CORRECT_RETURN_TYPE,
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
