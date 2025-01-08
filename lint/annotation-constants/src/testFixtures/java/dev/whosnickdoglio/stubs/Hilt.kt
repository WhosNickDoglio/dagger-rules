/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.stubs

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
            annotation class DefineComponent
        """
                .trimIndent()
        ),
    )
