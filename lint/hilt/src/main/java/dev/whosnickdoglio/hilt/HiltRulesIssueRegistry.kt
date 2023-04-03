/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.hilt

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import dev.whosnickdoglio.hilt.detectors.MissingHiltAnnotationDetector
import dev.whosnickdoglio.hilt.detectors.MissingInstallInDetector

class HiltRulesIssueRegistry : IssueRegistry() {

    override val issues: List<Issue> =
        listOf(
            MissingHiltAnnotationDetector.ISSUE,
            MissingInstallInDetector.ISSUE,
        )

    override val api: Int = CURRENT_API

    override val vendor: Vendor
        get() =
            Vendor(
                vendorName = "Nicholas Doglio",
                identifier = "dev.whosnickdoglio.dagger.rules:hilt-lint",
                feedbackUrl = "https://github.com/WhosNickDoglio/dagger-rules/issues/"
            )
}
