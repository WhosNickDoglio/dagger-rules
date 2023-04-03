/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.anvil

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import dev.whosnickdoglio.anvil.detectors.FavorContributesBindingOverBindsDetector
import dev.whosnickdoglio.anvil.detectors.MissingContributesBindingDetector
import dev.whosnickdoglio.anvil.detectors.MissingContributesToDetector

class AnvilRulesIssueRegistry : IssueRegistry() {

    override val issues: List<Issue> =
        listOf(
            FavorContributesBindingOverBindsDetector.ISSUE,
            MissingContributesToDetector.ISSUE,
            MissingContributesBindingDetector.ISSUE,
        )

    override val api: Int = CURRENT_API

    override val vendor: Vendor
        get() =
            Vendor(
                vendorName = "Nicholas Doglio",
                identifier = "dev.whosnickdoglio.dagger.rules:anvil-lint",
                feedbackUrl = "https://github.com/WhosNickDoglio/dagger-rules/issues"
            )
}
