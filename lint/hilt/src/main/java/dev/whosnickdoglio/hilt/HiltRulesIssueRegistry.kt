/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.hilt

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import com.google.auto.service.AutoService
import dev.whosnickdoglio.hilt.detectors.HiltAnnotationMustBeInterface
import dev.whosnickdoglio.hilt.detectors.MissingAndroidEntryPointDetector
import dev.whosnickdoglio.hilt.detectors.MissingHiltAndroidAppAnnotationDetector
import dev.whosnickdoglio.hilt.detectors.MissingHiltViewModelAnnotationDetector
import dev.whosnickdoglio.hilt.detectors.MissingInstallInDetector

@AutoService(IssueRegistry::class)
public class HiltRulesIssueRegistry : IssueRegistry() {
    override val issues: List<Issue> =
        listOf(
            HiltAnnotationMustBeInterface.ISSUE,
            MissingHiltAndroidAppAnnotationDetector.ISSUE,
            MissingAndroidEntryPointDetector.ISSUE,
            MissingHiltViewModelAnnotationDetector.ISSUE_MISSING_ANNOTATION,
            MissingHiltViewModelAnnotationDetector.ISSUE_UNNECESSARY_ANNOTATION,
            MissingInstallInDetector.ISSUE,
        )

    override val api: Int = CURRENT_API

    override val vendor: Vendor
        get() =
            Vendor(
                vendorName = "Nicholas Doglio",
                identifier = "dev.whosnickdoglio.dagger.rules:hilt-lint",
                feedbackUrl = "https://github.com/WhosNickDoglio/dagger-rules/issues/",
            )
}
