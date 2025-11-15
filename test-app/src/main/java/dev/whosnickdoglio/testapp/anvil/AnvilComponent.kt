// Copyright (C) 2025 Nicholas Doglio
// SPDX-License-Identifier: MIT

package dev.whosnickdoglio.testapp.anvil

import android.content.Context
import com.squareup.anvil.annotations.MergeComponent
import com.squareup.anvil.annotations.optional.SingleIn

@SingleIn(AppScope::class) @MergeComponent(AppScope::class) public interface AnvilComponent

public sealed interface AppScope

internal inline fun <reified T> Context.anvilInjector() = applicationContext as T
