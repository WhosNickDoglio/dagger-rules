/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.stubs

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles

val daggerAnnotations: TestFile =
  TestFiles.kotlin(
    """
        package  dagger

        annotation class Provides
        annotation class Binds
        annotation class Module
        annotation class Multibinds
        annotation class Component
        annotation class Subcomponent
    """
      .trimIndent(),
  )

val daggerMultibindingAnnotations: TestFile =
  TestFiles.kotlin(
    """
    package dagger.multibindings

    annotation class IntoMap
    annotation class IntoSet
    annotation class StringKey(val key: String)
    annotation class IntKey(val key: Int)
"""
      .trimIndent(),
  )

val daggerAssistedAnnotations: TestFile =
  TestFiles.kotlin(
    """
    package  dagger.assisted

    annotation class Assisted
    annotation class AssistedFactory
    annotation class AssistedInject
"""
      .trimIndent(),
  )

val javaxAnnotations: TestFile =
  TestFiles.kotlin(
    """
    package javax.inject

    annotation class Inject
    annotation class Scope
"""
      .trimIndent(),
  )
