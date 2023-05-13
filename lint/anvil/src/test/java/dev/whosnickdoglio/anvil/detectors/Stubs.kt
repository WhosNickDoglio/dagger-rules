/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.anvil.detectors

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles

val anvilAnnotations: TestFile =
    TestFiles.kotlin(
            """
    package com.squareup.anvil.annotations

    annotation class ContributesTo
    annotation class ContributesBinding
    annotation class ContributesMultibinding
    annotation class ContributesSubcomponent {
        annotation class Factory
    }
    annotation class MergeComponent
    annotation class MergeSubcomponent
"""
        )
        .indented()
