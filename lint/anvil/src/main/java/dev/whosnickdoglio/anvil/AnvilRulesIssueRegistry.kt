/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.anvil

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import com.google.auto.service.AutoService
import dev.whosnickdoglio.anvil.detectors.ContributesBindingMustHaveSuperDetector
import dev.whosnickdoglio.anvil.detectors.FavorContributesBindingOverBindsDetector
import dev.whosnickdoglio.anvil.detectors.MissingContributesBindingDetector
import dev.whosnickdoglio.anvil.detectors.MissingContributesToDetector
import dev.whosnickdoglio.anvil.detectors.NoAnvilInJavaDetector

@AutoService(IssueRegistry::class)
public class AnvilRulesIssueRegistry : IssueRegistry() {
    override val issues: List<Issue> =
        listOf(
            ContributesBindingMustHaveSuperDetector.ISSUE_BINDING_NO_SUPER,
            ContributesBindingMustHaveSuperDetector.ISSUE_CONTRIBUTES_TO_INSTEAD_OF_BINDING,
            FavorContributesBindingOverBindsDetector.ISSUE,
            NoAnvilInJavaDetector.ISSUE,
            MissingContributesToDetector.ISSUE,
            MissingContributesBindingDetector.ISSUE,
        )

    override val api: Int = CURRENT_API

    override val vendor: Vendor
        get() =
            Vendor(
                vendorName = "Nicholas Doglio",
                identifier = "dev.whosnickdoglio:anvil-lint",
                feedbackUrl = "https://github.com/WhosNickDoglio/dagger-rules/issues",
            )
}
