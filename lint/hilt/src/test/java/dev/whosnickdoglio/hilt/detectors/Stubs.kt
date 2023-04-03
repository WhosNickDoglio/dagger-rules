/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.hilt.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles

val hiltAnnotations =
    arrayOf(
        TestFiles.kotlin(
                """
    package dagger.hilt.android

    annotation class AndroidEntryPoint
    annotation class HiltAndroidApp
"""
            )
            .indented(),
        TestFiles.kotlin(
            """
            package dagger.hilt.android.lifecycle

            annotation class HiltViewModel
        """
                .trimIndent()
        ),
        TestFiles.kotlin(
            """
            package dagger.hilt

            annotation class InstallIn
            annotation class EntryPoint
        """
                .trimIndent()
        ),
    )
