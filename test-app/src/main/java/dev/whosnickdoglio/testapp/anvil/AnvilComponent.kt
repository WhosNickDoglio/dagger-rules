/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

package dev.whosnickdoglio.testapp.anvil

import android.content.Context
import com.squareup.anvil.annotations.MergeComponent
import com.squareup.anvil.annotations.optional.SingleIn

@SingleIn(AppScope::class) @MergeComponent(AppScope::class) interface AnvilComponent

sealed interface AppScope

inline fun <reified T> Context.anvilInjector() = applicationContext as T
