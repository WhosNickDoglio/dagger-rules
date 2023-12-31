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

    import kotlin.reflect.KClass

    annotation class ContributesTo(val scope: KClass<*> = Int::class)
    annotation class ContributesBinding(val scope: KClass<*> = Int::class, boundType: KClass<*> = Unit::class)
    annotation class ContributesMultibinding(val scope: KClass<*> = Int::class, boundType: KClass<*> = Unit::class)
    annotation class ContributesSubcomponent {
        annotation class Factory
    }
    annotation class MergeComponent(val scope: KClass<*> = Int::class)
    annotation class MergeSubcomponent(val scope: KClass<*> = Int::class)
"""
        )
        .indented()
